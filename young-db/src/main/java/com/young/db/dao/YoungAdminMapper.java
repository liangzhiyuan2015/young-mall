package com.young.db.dao;

import com.young.db.entity.YoungAdmin;
import com.young.db.entity.YoungAdminExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface YoungAdminMapper {
    long countByExample(YoungAdminExample example);

    int deleteByExample(YoungAdminExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(YoungAdmin record);

    int insertSelective(YoungAdmin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table young_admin
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    YoungAdmin selectOneByExample(YoungAdminExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table young_admin
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    YoungAdmin selectOneByExampleSelective(@Param("example") YoungAdminExample example, @Param("selective") YoungAdmin.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table young_admin
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<YoungAdmin> selectByExampleSelective(@Param("example") YoungAdminExample example, @Param("selective") YoungAdmin.Column ... selective);

    List<YoungAdmin> selectByExample(YoungAdminExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table young_admin
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    YoungAdmin selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") YoungAdmin.Column ... selective);

    YoungAdmin selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table young_admin
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    YoungAdmin selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    int updateByExampleSelective(@Param("record") YoungAdmin record, @Param("example") YoungAdminExample example);

    int updateByExample(@Param("record") YoungAdmin record, @Param("example") YoungAdminExample example);

    int updateByPrimaryKeySelective(YoungAdmin record);

    int updateByPrimaryKey(YoungAdmin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table young_admin
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") YoungAdminExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table young_admin
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByPrimaryKey(Integer id);
}