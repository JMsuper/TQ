package user.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import user.domain.User;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        // DaoFactory처럼 @Configuration이 붙은 자바코드를 설정정보로 사용하려면 AnnotationConfigApplicationContext를 사용한다.
        ApplicationContext context =
                new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao = context.getBean("userDao",UserDao.class);

        // getBean() 메소드는 ApplicationContext가 관리하는 오브젝트를 요청하는 메소드
        // "userDao"는 ApplicationContext에 저장된 bean의 이름
        // DaoFactory에서 @Bean이라는 어노테이션을 userDao라는 이름의 메소드에 붙였는데,
        // 이때 메소드의 이름이 빈 객첵의 이름이 된다.
        // userDao라는 이름의 빈을 가져오는 것은 userDao()의 리턴값을 가져온다고 생각하자
        // 원래 getBean()은 Object type을 리턴한다. 따라서 이를 형변환시켜주시위해
        // 2번째 파라미터로 UserDao.class를 넣주는 것이다.

        User user = new User();
        user.setId("1");
        user.setName("KJM");
        user.setPassword("123");

        dao.add(user);

        System.out.println(user.getId() + "등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getId() + "조회 성공");
    }
}
