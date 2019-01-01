package org.lyg.vpc.process.companyImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import org.json.JSONObject;
import org.lyg.common.utils.StringUtil;
import org.lyg.common.utils.TokenUtil;
import org.lyg.vpc.controller.company.LoginService;
import org.lyg.vpc.controller.factory.LoginDAO;
import org.lyg.vpc.view.Usr;
import org.lyg.vpc.view.UsrToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private LoginDAO loginDAO;

	@Override
	public Usr findUsrByUEmail(String uEmail) throws IOException {
		Usr usr = loginDAO.selectUsrByUEmail(uEmail);
		return usr;
	}

	@Override
	public UsrToken findUsrTokenByUId(Integer uId) throws IOException {
		UsrToken usrToken = loginDAO.selectUsrTokenByUId(uId);
		return usrToken;
	}

	@Override
	public void updateUsrTokenByUId(Integer uId, String key, String uPassword, long uTime) throws IOException {
		loginDAO.updateUsrTokenByUId(uId, key, uPassword, uTime);
	}

	@Override
	public void insertRowByTablePath(String baseName, String tableName, JSONObject jsobj) throws FileNotFoundException, IOException {
		loginDAO.insertRowByTablePath(baseName, tableName, jsobj);
	}

	@Override
	public String checkStatus(String token, String level) throws Exception {
		if (null == token) {
			return "invalid ��Կ��ʧ�����µ�½��";
		}
		String json = StringUtil.decode(token);
		JSONObject js;
		try {
			js = new JSONObject(json);
		}catch(Exception e) {
			return "invalid ��Կ���������µ�½��";
		}
		long uTime = js.getLong("uTime");
		String uPassword = js.getString("mPassword");
		String uEmail = js.getString("uEmail");
		Usr usr = this.findUsrByUEmail(uEmail);
		UsrToken usrToken = this.findUsrTokenByUId(usr.getuId());
		String password = TokenUtil.getFirstMD5Password(js.getString("uKey"), usrToken.getuPassword());
		if (!uPassword.equals(password)) {
			return "invalid �������";
		}
		long nowTime = new Date().getTime();
		if(uTime + 600000 < nowTime) {
			return "invalid 10���ӳ�ʱ�������µ�½��";
		}
		
		if(level.contains("level")) {
			String uLevel = usrToken.getuLevel();
			if(!uLevel.contains("high")) {
				return "invalid Ȩ�޲���";
			}
		}
		return "valid";
	}
}