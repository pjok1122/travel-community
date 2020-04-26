package project.board.util;

import org.springframework.stereotype.Component;

import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;

import project.board.domain.dto.GpsDecimal;

@Component
public class GpsExtractUtils {
	
	private String match = "[^\uAC00-\uD7A3xfe0-9\\.\\s]";
	
	public GpsDecimal toGpsDecimal(Metadata metadata) {
		GpsDirectory gps = metadata.getFirstDirectoryOfType(GpsDirectory.class);
		//GPS 정보가 없다면 빈 객체 반환
		if(gps==null) return new GpsDecimal();
		
		//GPS 정보 추출
		Double latitude = GpsToDecimal(gps.getDescription(GpsDirectory.TAG_LATITUDE_REF), gps.getDescription(GpsDirectory.TAG_LATITUDE));
		Double longitude = GpsToDecimal(gps.getDescription(GpsDirectory.TAG_LONGITUDE_REF), gps.getDescription(GpsDirectory.TAG_LONGITUDE));
		
		//시간정보 추출
//		ExifSubIFDDirectory exif = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
//		Date date = exif.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
		
		return new GpsDecimal(latitude, longitude);
	}
	
	private Double GpsToDecimal(String direction, String gpsInfo) {
		double decimal = 0;
		String[] dms = gpsInfo.replaceAll(match, "").split(" ");
		decimal = Double.parseDouble(dms[0]) + Double.parseDouble(dms[1])/60 + Double.parseDouble(dms[2])/3600;
		
		if(direction.equals("N") || direction.equals("E")) {
			return decimal;
		}
		else {
			return -decimal;
		}
		
	}

}

