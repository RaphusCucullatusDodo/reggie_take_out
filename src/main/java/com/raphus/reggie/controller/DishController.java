package com.raphus.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.raphus.reggie.common.R;
import com.raphus.reggie.dto.DishDto;
import com.raphus.reggie.entity.Category;
import com.raphus.reggie.entity.Dish;
import com.raphus.reggie.entity.DishFlavor;
import com.raphus.reggie.service.CategoryService;
import com.raphus.reggie.service.DishFlavorService;
import com.raphus.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @Transactional
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        //清理所有相关的缓存
//        Set keys = redisTemplate.keys("dish_*");
//        Set keys1 = redisTemplate.keys("*_dish_*");
//        redisTemplate.delete(keys);
//        redisTemplate.delete(keys1);
        //精确清理缓存
        Set keys = redisTemplate.keys("dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus());
        redisTemplate.delete(keys);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝 pageInfo->dishDtoPage ,且排除records属性
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //将item中的属性赋值给dishDto的同名属性
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                //获取分类名
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);
        //清理所有相关的缓存
//        Set keys = redisTemplate.keys("dish_*");
//        Set keys1 = redisTemplate.keys("*_dish_*");
//        redisTemplate.delete(keys);
//        redisTemplate.delete(keys1);
        //精确清理缓存
        Set keys = redisTemplate.keys("dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus());
        redisTemplate.delete(keys);
        return R.success("新增菜品成功");
    }

//    /**
//     * 按条件获取菜品列表
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        Long categoryId = dish.getCategoryId();
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dishLambdaQueryWrapper.eq(categoryId!=null,Dish::getCategoryId, categoryId)
//                //状态为起售(即=1)
//                .eq(Dish::getStatus,1)
//                //按最后更新时间排序
//                .orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
//
//        return R.success(list);
//    }

    /**
     * 按条件获取菜品列表
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        Long categoryId = dish.getCategoryId();

        List<DishDto> dishDtoList;
        //查询redis中是否存在该数据
        //表示dish表按category与status查找的缓存
        String key = "dish_" + categoryId + "_" + dish.getStatus();
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //存在则直接返回数据
        if (dishDtoList!=null){
            return R.success(dishDtoList);
        }
        //redis中不存在则查询数据库
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(categoryId!=null,Dish::getCategoryId, categoryId)
                //状态为起售(即=1)
                .eq(Dish::getStatus,1)
                //按最后更新时间排序
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(dishLambdaQueryWrapper);

        dishDtoList = list.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, item.getId()));
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        //将查询结果存入Redis
        redisTemplate.opsForValue().set(key,dishDtoList,1, TimeUnit.HOURS);
        return R.success(dishDtoList);
    }

}
