package project.board.domain.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import project.board.domain.Article;
import project.board.domain.CommonDomain;

@Setter
@Getter
public class Page {
	int recordsPerPage;
	int firstPageNo;
	int prevPageNo;
	int startPageNo;
	int currentPageNo;
	int endPageNo;
	int nextPageNo;
	int finalPageNo;
	int numberOfRecords;
	int sizeOfPage;
	int offset;

	List<? extends CommonDomain> list;
	
	public Page(int currentPageNo) {
		this.currentPageNo = currentPageNo;
		this.recordsPerPage = 10;
		this.sizeOfPage = 3;
	}
	
	public Page(int currentPageNo, int numberOfRecords) {
		this(currentPageNo);
		this.numberOfRecords = numberOfRecords;
		makePageInfo();
	}
	
	public void setNumberOfRecordsAndMakePageInfo(int numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
		makePageInfo();
	}
	
	public void makePageInfo() {
		if(numberOfRecords==0) { 		//게시물이 없는 경우
			return ;
		}
		if(currentPageNo <=0){
			currentPageNo= 1;			//기본 값 설정
			firstPageNo = 1;			//기본 값 설정
		}
		
		if(recordsPerPage == 0) {
			recordsPerPage = 10;		//기본 값 설정
		}
		
		finalPageNo = (numberOfRecords + (recordsPerPage -1)) / recordsPerPage;
		
		if(currentPageNo > finalPageNo) {
			currentPageNo = finalPageNo;
		}
		
		startPageNo = ((currentPageNo-1) / sizeOfPage) * sizeOfPage + 1; //한 번에 보여지는 페이지 번호가 3개씩이라면, startPageNo= 1+3x 꼴이 된다.
		endPageNo = (endPageNo > finalPageNo) ? finalPageNo : startPageNo + sizeOfPage - 1;
		
		prevPageNo = (currentPageNo==1) ? 1 : currentPageNo-1;
		nextPageNo = (currentPageNo==finalPageNo) ? finalPageNo : currentPageNo+1;
		
		offset = (currentPageNo-1)*recordsPerPage;
		
	}
	
}
