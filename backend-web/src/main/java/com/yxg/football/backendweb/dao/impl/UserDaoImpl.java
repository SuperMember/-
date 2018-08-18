package com.yxg.football.backendweb.dao.impl;


import com.yxg.football.backendweb.dao.BaseDao;
import com.yxg.football.backendweb.dao.UserDao;
import com.yxg.football.backendweb.entity.Permission;
import com.yxg.football.backendweb.entity.Role;
import com.yxg.football.backendweb.entity.User;
import com.yxg.football.backendweb.enums.CircleEnum;
import com.yxg.football.backendweb.enums.RoleEnum;
import com.yxg.football.backendweb.exceptions.CircleException;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImpl extends BaseDao implements UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    Md5PasswordEncoder md5PasswordEncoder;

    public User getUserByName(String username) {
        List<User> list = jdbcTemplate.query("select * from all_user where username=?", new Object[]{username}, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setId(resultSet.getInt("ID"));
                user.setUsername(resultSet.getString("USERNAME"));
                user.setPassword(resultSet.getString("PASSWORD"));
                user.setCreated(resultSet.getDate("CREATED"));
                user.setDegree(resultSet.getString("DEGREE"));
                user.setStatue(resultSet.getInt("STATUE"));
                user.setPhone(resultSet.getString("PHONE"));
                user.setSex(resultSet.getInt("SEX"));
                user.setImg(resultSet.getString("IMG"));
                return user;
            }
        });
        if (list.size() != 0) {
            return list.get(0);
        }
        return null;
    }

    public List<Role> getRolesByName(String username) {
        return jdbcTemplate.query("select r.* from all_role_user lr,all_role r where  lr.ROLE_ID=r.ID and lr.username=?", new Object[]{username}, new RowMapper<Role>() {
            @Override
            public Role mapRow(ResultSet resultSet, int i) throws SQLException {
                Role role = new Role();
                role.setId(resultSet.getInt("ID"));
                role.setRolename(resultSet.getString("ROLENAME"));
                return role;
            }
        });
    }

    public List<Permission> getPermissionsByRoleName(String roleName) {
        return jdbcTemplate.query("select p.* from all_permission p,all_role_permission rp where rp.PERMISSION_ID=p.ID and rp.ROLENAME=?", new Object[]{roleName}, new RowMapper<Permission>() {
            @Override
            public Permission mapRow(ResultSet resultSet, int i) throws SQLException {
                Permission permission = new Permission();
                permission.setId(resultSet.getInt("ID"));
                permission.setPermissionName(resultSet.getString("PERMISSION_NAME"));
                return permission;
            }
        });
    }

    @Transactional
    @Override
    public Integer updateUserInfo(Map<String, Object> params) {
        if (params == null) {
            throw new CircleException(CircleEnum.PARAM_NULL.getMsg());
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sql = "update all_user set IMG=? ,SEX=?,PHONE = ?,USERNAME = ? where ID = ?";
        String img = (String) params.get("img");
        Integer sex = (Integer) params.get("sex");
        String phone = (String) params.get("phone");
        String username = (String) params.get("username");
        return this.getJdbcTemplate().update(sql, new Object[]{img, sex, phone, username, user.getId()});
    }


    /*l
    * 验证手机是否有效
    * */
    @Override
    public Boolean checkPhone(String phone) {
        String sql = "select * from all_user where PHONE=?";
        List<Map<String, Object>> resultList = this.getJdbcTemplate().queryForList(sql, new Object[]{phone});
        if (resultList != null && resultList.size() != 0) {
            return true;
        }
        return false;
    }


    /*
    * 创建用户
    * */
    @Transactional
    @Override
    public Integer createUser(Map<String, Object> params) {
        if (params == null) {
            throw new CircleException(CircleEnum.PARAM_NULL.getMsg());
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "insert into all_user(PHONE,USERNAME,PASSWORD,SEX) values(?,?,?,?)";
        String username = (String) params.get("username");
        String password = md5PasswordEncoder.encodePassword((String) params.get("password"), null);
        Integer sex = (Integer) params.get("sex");
        String phone = (String) params.get("phone");
        this.getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setInt(3, sex);
                preparedStatement.setString(4, phone);
                return preparedStatement;
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    /*
    * 检验用户名唯一性
    * */
    @Override
    public Boolean checkUserName(String username) {
        String sql = "select * from all_user where USERNAME = ? ";
        List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, new Object[]{username});
        if (list != null && list.size() != 0) {
            return true;
        }
        return false;
    }

    /*
    * 获取用户信息
    * */
    @Override
    public List<Map<String, Object>> getUserInfo(Integer userId) {
        String sql = "select ID,IMG,USERNAME,CREATED,SEX,STATUE,DEGREE,PHONE,POINTS from all_user where ID = ? ";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{userId});
    }

    /*
    * 修改密码
    * */
    @Override
    public Integer updateSecret(String phone, String password) {
        String sql = "update all_user set PASSWORD = ? where PHONE = ?";
        return this.getJdbcTemplate().update(sql, new Object[]{md5PasswordEncoder.encodePassword(password, null), phone});
    }

    /*
    * 关注用户
    * */
    @Override
    public Integer focusUser(Integer userId, Integer beuserId) {
        String sql = "insert into wechat_user_focus(USERID,BEUSERID,CREATED) values(?,?,?)";
        Date created = new Date();
        return this.getJdbcTemplate().update(sql, new Object[]{userId, beuserId, created});
    }

    /*
    * 取消关注
    * */
    @Override
    public Integer unFocusUser(Integer userId, Integer beuserId) {
        String sql = "delete from wechat_user_focus where USERID = ? and BEUSERID = ?";
        return this.getJdbcTemplate().update(sql, new Object[]{userId, beuserId});
    }

    @Override
    public List<Map<String, Object>> getFocusUser(Integer userId) {
        String sql = "select u.IMG,u.ID,u.USERNAME from wechat_user_focus wuf,all_user u where u.ID = wuf.BEUSERID and wuf.USERID = ?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{userId});
    }

    @Override
    public List<Map<String, Object>> getFans(Integer userId) {
        String sql = "select u.IMG,u.ID,u.USERNAME from wechat_user_focus wuf,all_user u where u.ID = wuf.USERID and wuf.BEUSERID = ?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{userId});
    }

    /*
    * 获取用户状态
    * */
    @Override
    public Integer getUserStatue(Integer userId) {
        String sql = "select STATUE from all_user where ID = ?";
        return this.getJdbcTemplate().queryForObject(sql, new Object[]{userId}, Integer.class);
    }

    @Override
    public Integer getUserPoint(Integer userId) {
        String sql = "select POINTS from all_user where ID = ?";
        return this.getJdbcTemplate().queryForObject(sql, new Object[]{userId}, Integer.class);
    }

    /*
    * 角色表
    * */
    @Override
    public Integer saveRole(String username, Integer userId) {
        String sql = "insert into all_user(USERNAME,USER_ID,ROLE_ID) values(?,?,?)";
        return this.getJdbcTemplate().update(sql, new Object[]{username, userId, RoleEnum.ROLE_OWNER.getCode()});
    }

    /*
    * 判断是否有圈主角色
    * */
    @Override
    public Boolean checkRole(Integer cId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sql = "select * from wechat_circle where USER_ID = ? and ID = ?";
        List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, new Object[]{user.getId(), cId});
        if (list != null && list.size() != 0) {
            return true;
        }
        return false;
    }
}
