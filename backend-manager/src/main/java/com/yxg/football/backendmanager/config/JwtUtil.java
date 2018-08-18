package com.yxg.football.backendmanager.config;

import com.yxg.football.backendmanager.entity.User;
import com.yxg.football.backendmanager.util.Const;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    public static final String TOKEN_SECRET = Const.KEY;//密钥
    public static final Integer expiration = Const.EXPIRATIOIN;

    //生成jwt字符串
    public String generateToken(Map<String, Object> claims) {
        String token = Jwts.builder().setClaims(claims)
                .setExpiration(generateDate())
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
                .compact();
        return token;
    }

    public Date generateDate() {
        Date date = new Date(System.currentTimeMillis() + this.expiration * 1000);
        return date;
    }

    public String generateToken(User sysUser) {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("sub", sysUser.getUsername());
        claims.put("created", this.generateDate());
        //不应该放密码
        //claims.put("password", sysUser.getPassword());
        return this.generateToken(claims);
    }

    //解析
    public Claims getClaimsByToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(TOKEN_SECRET)
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    //获取用户名称
    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = this.getClaimsByToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    //获取密码
    /*public String getPasswordFromToken(String token) {
        String password;
        try {
            final Claims claims = this.getClaimsByToken(token);
            password = (String) claims.get("password");
        } catch (Exception e) {
            password = null;
        }
        return password;
    }*/

    //判断是否过期
    private Boolean isTokenExpired(String token) {
        final Date expirationDate = this.getExpirationDateFromToken(token);
        //return expiration.before(this.generateDate());
        return (new Date().getTime() - expirationDate.getTime()) <= expiration;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = this.getClaimsByToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    //获取jwt token创建时间
    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsByToken(token);
            created = new Date((Long) claims.get("created"));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    //验证时间
//    public boolean isCreatedBeforeLastPasswordReset(Date create, Date expiration) {
//        boolean isValid = (create != null && create.before(expiration));
//        return isValid;
//    }

    //验证token是否有效
    public Boolean validateToken(String token, UserDetails userDetails) {
        User user = (User) userDetails;
        final String username = this.getUsernameFromToken(token);
//      final String password = this.getPasswordFromToken(token);
        //判断token是否有效
        boolean isTokenExpired = this.isTokenExpired(token);
        //获取创建时间
        Date created = this.getCreatedDateFromToken(token);
        //        return (username.equals(user.getUsername())
//                && !isTokenExpired && password.equals(user
//                .getPassword()));
        //不通过匹配密码是否正确，通过时间来验证是否有效
        return (username.equals(user.getUsername()) && isTokenExpired
                //&& isCreatedBeforeLastPasswordReset(getCreatedDateFromToken(token),
        );
    }
}
