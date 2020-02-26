package project.board.enums;

import lombok.Getter;

@Getter
public enum Nation {
	ALL("ALL","전체"),
	JP("JAPAN", "일본"),				//일본
	TW("TAIWAN", "대만"),				//대만
	LA("LAOS", "라오스"),				//라오스
	VN("VIETNAM", "베트남"),			//베트남
	SG("SINGAPORE", "싱가폴"),		//싱가폴
	MY("MALAYSIA", "말레이시아"),		//말레이시아
	TH("THAILAND", "태국"),			//태국
	CN("CHINA", "중국");				//중국
	
	private String fullName;
	private String krValue;
	
	private Nation(String fullName, String krValue) {
		this.fullName = fullName;
		this.krValue = krValue;
	}
}

