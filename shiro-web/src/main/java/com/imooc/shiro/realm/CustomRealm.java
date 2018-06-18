package com.imooc.shiro.realm;

import com.imooc.dao.UserDao;
import com.imooc.vo.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;
import java.util.*;

public class CustomRealm extends AuthorizingRealm {

    @Resource
    private UserDao userDao;
   /* Map<String,String> userMap = new HashMap<String, String>(16);

    {
        userMap.put("mark","debd83bca36c128b8ea06d94ef54d551");
        super.setName("customRealm");
    }*/
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        String userName = (String) principalCollection.getPrimaryPrincipal();
        //从数据库或者缓存获取角色数据
        Set<String> roles = getRolesByUserName(userName);
        Set<String> permissions = getPermissionsByUserName(userName);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    private Set<String> getPermissionsByUserName(String userName) {
        Set<String> sets = new HashSet<String>();
        sets.add("user:add");
        sets.add("user:delete");
        return sets;
    }

    private Set<String> getRolesByUserName(String userName) {
       /* Set<String> sets = new HashSet<String>();
        sets.add("admin");
        sets.add("user");*/
        System.out.println("从数据库获取授权数据");
        List<String> list = userDao.queryRolesByUserName(userName);
        Set<String> sets = new HashSet<String>(list);
        return sets;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        //1.从主体传过来的认证信息中，获得用户名
        String userName = (String) authenticationToken.getPrincipal();
        //2.通过用户名到数据库中获得凭证
        String password = getPasswordByUserName(userName);
        if(password == null){
            return null;
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(userName,password,"customRealm");
        simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(userName));
        return simpleAuthenticationInfo;
    }

    /**
     * 模拟数据库查询凭证
     * @param userName
     * @return
     */
    private String getPasswordByUserName(String userName) {
        User user = userDao.getUserByUserName(userName);
        if(user != null){
            return user.getPassword();
        }
        return null;
    }

    public static void main(String[] args) {
        Md5Hash md5Hash = new Md5Hash("1234567","mark");
        System.out.println(md5Hash.toString());
    }
}
