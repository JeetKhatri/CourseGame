package root.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import root.Bean.UserBean;
import root.Controller.SendEmail;
import root.Utils.DBConnection;
import root.Utils.GenrateMathodsUtils;

public class TADAO {

	ResultSet rs = null;
	PreparedStatement pstmt = null;
	Connection conn = null;

	public ArrayList<UserBean> getList() {
		ArrayList<UserBean> list = new ArrayList<UserBean>();
		UserBean Bean = new UserBean();
		String sql = "select * from users";
		conn = DBConnection.getConnection();

		boolean flag = false;
		if (conn != null) {

			try {
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();

				while (rs.next()) {
					Bean = new UserBean();
					Bean.setEmailId(rs.getString("emailid"));
					Bean.setUserId(rs.getString("userId"));
					Bean.setUserIsAvailable(rs.getString("isAvailable"));
					Bean.setUserName(rs.getString("name"));
					Bean.setUserRole(rs.getString("role"));
					Bean.setUserResponseStatus(true);
					list.add(Bean);
					flag = true;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		return list;

	}

	public boolean insert(UserBean userBean, String degree) {

		String sql = "insert into users(userId,emailid,name,role,isAvailable,password) values(?,?,?,?,?,?)";
		conn = DBConnection.getConnection();
		String id = GenrateMathodsUtils.getRandomString(15);
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, id);
				pstmt.setString(2, userBean.getEmailId());
				pstmt.setString(3, userBean.getUserName());
				pstmt.setString(4, userBean.getUserRole());
				pstmt.setString(5, "N");
				pstmt.setString(6, "hekdnkndfk");
				int no = pstmt.executeUpdate();
				if (no != 0) {

					pstmt = conn.prepareStatement(
							"insert into Faculty (facultyid,userId,degree,isApproved) values(?,?,?,?)");
					pstmt.setString(1, GenrateMathodsUtils.getRandomString(15));
					pstmt.setString(2, id);
					pstmt.setString(3, degree);
					pstmt.setString(4, "N");
					if (pstmt.executeUpdate() == 0) {
						conn.rollback();
						return false;
					} else {
						SendEmail obj = new SendEmail();
						obj.SendEmail("Request arrive", userBean.getEmailId(), "Request arrive we accept your requst");
						return true;
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					conn.commit();
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
