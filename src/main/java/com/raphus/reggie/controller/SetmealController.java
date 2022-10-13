package com.raphus.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.raphus.reggie.common.R;
import com.raphus.reggie.dto.SetmealDto;
import com.raphus.reggie.entity.Category;
import com.raphus.reggie.entity.Setmeal;
import com.raphus.reggie.service.CategoryService;
import com.raphus.reggie.service.SetmealDishService;
import com.raphus.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */

@Slf4j
@Api(tags = "套餐相关接口")
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache" ,allEntries = true)
    @ApiOperation("新增套餐接口")
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required = true),
            @ApiImplicitParam(name="pageSize",value="每页记录数",required=true),
            @ApiImplicitParam(name="name",value = "套餐名称",required =false)
    })
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器对象
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(setmealPage,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(setmealPage,dtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache" ,allEntries = true)
    @ApiOperation("删除套餐接口")
    @ApiImplicitParam(name="ids",value = "套餐id列表",required = true)
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);

        setmealService.removeWithDish(ids);

        return R.success("套餐数据删除成功");
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(cacheNames = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status" )
    @ApiOperation("套餐条件查询接口")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        log.info("setmeal:{}", setmeal);
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(setmeal.getName()), Setmeal::getName, setmeal.getName())
                .eq(null != setmeal.getCategoryId(), Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(null != setmeal.getStatus(), Setmeal::getStatus, setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
