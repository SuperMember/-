package com.yxg.football.backendmanager.dao.impl;

import com.yxg.football.backendmanager.config.JwtUtil;
import com.yxg.football.backendmanager.dao.BaseDao;
import com.yxg.football.backendmanager.dao.UserDao;
import com.yxg.football.backendmanager.entity.Permission;
import com.yxg.football.backendmanager.entity.Role;
import com.yxg.football.backendmanager.entity.User;
import com.yxg.football.backendmanager.enums.UserEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImpl extends BaseDao implements UserDao {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public List<Map<String, Object>> getAllUser(Integer statue, Integer page, Integer size) {
        StringBuilder sql = new StringBuilder("select * from all_user ");
        List<Object> args = new ArrayList<>();
        if (statue == UserEnum.USER_ALL.getCode()) {
            //查找全部
            sql.append(" order by CREATED limit ?,?");
            args.add((page - 1) * size);
            args.add(size);
        } else {
            sql.append("where STATUE = ? order by CREATED limit ?,?");
            args.add(statue);
            args.add((page - 1) * size);
            args.add(size);
        }
        return this.getJdbcTemplate().queryForList(new String(sql), args.toArray());
    }

    /*
    * 修改用户状态(小黑屋,正常状态)
    * */
    @Override
    public Integer setUserStatue(Integer userId, Integer statue) {
        String sql = "update all_user set STATUE=? where ID=?";
        return this.getJdbcTemplate().update(sql, new Object[]{statue, userId});
    }

    /*
    * 获取某个用户的评论
    * */
    @Override
    public List<Map<String, Object>> getCommentByUserId(Integer userId, Integer page, Integer size) {
       // String sql = "select * from wechat_comment where USER_ID = ? and TYPE = 2 limit ?,?";
        String sql = "select wc.* from vue_article v, wechat_comment wc where v.USERID = ? and v.ID = wc.BELONG_ID and wc.TYPE = 2 limit ?,?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{userId, (page - 1) * size, size});
    }

    /*
    * 根据id获取用户信息
    * */
    @Override
    public List<Map<String, Object>> getUserById(Integer userId) {
        String sql = "select * from all_user where ID = ?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{userId});
    }

    /*
    * 用户总数
    * */
    @Override
    public Integer getUsersCount(Integer statue) {
        StringBuilder sql = new StringBuilder("select count(*) from all_user where 1=1 ");
        List<Object> args = new ArrayList<>();
        if (statue != UserEnum.USER_ALL.getCode()) {
            sql.append(" and STATUE = ?");
            args.add(statue);
        }
        return this.getJdbcTemplate().queryForObject(new String(sql), args.toArray(), Integer.class);
    }

    /*
    * 修改用户头像
    * */
    @Override
    public Integer updateProfile(String url, Integer userId) {
        String sql = "UPDATE all_user set IMG = ? where ID = ?";
        return this.getJdbcTemplate().update(sql, new Object[]{url, userId});
    }


    public User getUserByName(String username) {
        //获取用户名称
        List<User> list = this.getJdbcTemplate().query("select * from all_user where username=?", new Object[]{username}, new RowMapper<User>() {
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
        return this.getJdbcTemplate().query("select r.* from all_role_user lr,all_role r where  lr.ROLE_ID=r.ID and lr.username=?", new Object[]{username}, new RowMapper<Role>() {
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
        return this.getJdbcTemplate().query("select p.* from all_permission p,all_role_permission rp where rp.PERMISSION_ID=p.ID and rp.ROLENAME=?", new Object[]{roleName}, new RowMapper<Permission>() {
            @Override
            public Permission mapRow(ResultSet resultSet, int i) throws SQLException {
                Permission permission = new Permission();
                permission.setId(resultSet.getInt("ID"));
                permission.setPermissionName(resultSet.getString("PERMISSION_NAME"));
                return permission;
            }
        });
    }

    /*
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
}
