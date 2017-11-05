package root.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import root.Bean.TABean;
import root.Bean.UserBean;
import root.Controller.SendEmail;
import root.Utils.DBConnection;
import root.Utils.GenrateMathodsUtils;

public class UserDAO {

	ResultSet rs = null;
	PreparedStatement pstmt = null;
	Connection conn = null;

	public ArrayList<UserBean> getList() {
		ArrayList<UserBean> list = new ArrayList<UserBean>();
		UserBean albumBean = new UserBean();
		String sql = "select * from users";
		conn = DBConnection.getConnection();

		boolean flag = false;
		if (conn != null) {

			try {
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();

				while (rs.next()) {
					albumBean = new UserBean();
					albumBean.setEmailId(rs.getString("emailid"));
					albumBean.setUserId(rs.getString("userId"));
					albumBean.setUserIsAvailable(rs.getString("isAvailable"));
					albumBean.setUserName(rs.getString("name"));
					albumBean.setUserRole(rs.getString("role"));
					albumBean.setUserResponseStatus(true);
					list.add(albumBean);
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

	public boolean insertFaculty(UserBean userBean, String degree) {

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
				pstmt.setString(4, "Faculty");
				pstmt.setString(5, "N");
				pstmt.setString(6, GenrateMathodsUtils.makeSHA512("hekdnknd0@#fk"));
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

	public ArrayList<TABean> insertTA(UserBean userBean, String batchId) {

		ArrayList<TABean> beans = new ArrayList<TABean>();
		TABean bean = new TABean();
		String random = GenrateMathodsUtils.getRandomString(7);
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
				pstmt.setString(4, "TA");
				pstmt.setString(5, "Y");
				pstmt.setString(6, GenrateMathodsUtils.makeSHA512(random));
				int no = pstmt.executeUpdate();
				if (no != 0) {

					pstmt = conn.prepareStatement("insert into ta (taid,userId,batchId) values(?,?,?)");
					String taid = GenrateMathodsUtils.getRandomString(15);
					pstmt.setString(1, taid);
					pstmt.setString(2, id);
					pstmt.setString(3, batchId);
					if (pstmt.executeUpdate() == 0) {
						conn.rollback();
						beans.add(new TABean());
					} else {
						bean.setBatchid(batchId);
						bean.setTaid(taid);
						bean.setUserid(id);

						SendEmail obj = new SendEmail();
						obj.SendEmail("Request accepted", getTEmail(taid),
								"TA Request arrive we accept your requst & password is " + random);
						beans.add(bean);
						conn.commit();
						conn.setAutoCommit(true);
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return beans;
	}

	private String getTEmail(String id) {
		String sql = "select * from ta,users where users.userid = ta.userid and taid = ?";

		if (conn != null) {

			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();

				while (rs.next()) {
					return rs.getString("emailid")+"";
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				
			}

		}
		return null;

	}

}