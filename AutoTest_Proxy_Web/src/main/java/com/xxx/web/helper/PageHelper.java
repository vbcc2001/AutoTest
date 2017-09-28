
package com.xxx.web.helper;

/**
 * 用于WEB分页的一个通用类，包含4个属性：<br>
 * 总条数（allCount）,allPage(总页数),currentPage(当前页),pageSize(每页显示数)<br>
 * 关系:allPage = (allCount+pageSize-1)/pageSize
 * @author 门士松 ,2013/01/14
 * @version 1.00
 * @since
 */

public class PageHelper {
	
	//每页显示的数量
	private int pageCount ;
	//当前页
	private int currentPage ;
	//总页数
	private int allPage ;
	//总记录条数
	private int allCount;
		
	/**
	 * 带参构造函数,当前页默认为1
	 * @param allCount 设置总条数,应>=0条,负数自动设为0
	 * @param pageSize	设置每页显示数,应>=1条,否则自动设置为10
	 */
	public  PageHelper(int allCount,int pageCount){
		this.currentPage = 1;
		this.pageCount =pageCount;
		this.allCount = allCount;
		this.allPage = (this.allCount+this.pageCount-1)/this.pageCount;
	}
	/**
	 * 无参构造函数,默认:当前页为1,每页显示数为5,总条数,总页数为0
	 */
	public  PageHelper(){
		this(0,5);
	}
	/**
	 * 获得每页显示数
	 * @return 每页显示数
	 */
	public int getPageCount() {
		return pageCount;
	}
	/**
	 * 设置每页显示数
	 * @param pageCount 每页显示数,应>=1条,否则无效
	 */
	public void setPageCount(int pageCount) {
		if(pageCount>=1){
			this.pageCount = pageCount;
		}
		this.allPage = (this.allCount+this.pageCount-1)/this.pageCount;
	}
	/**
	 * 获得当前页
	 * @return	当前页
	 */
	public int getCurrentPage() {
		return currentPage;
	}
	
	/**
	 * 设置当前页
	 * @param currentPage 当前页,>=1,<=总页数,否则无效,
	 * 
	 */
	public void setCurrentPage(int currentPage) {
		if(currentPage >= 1 && currentPage<=this.allPage){
			this.currentPage = currentPage;
		}
	}
	
	/**
	 *	获得总页数
	 * @return	总页数
	 */
	public int getAllPage() {
		return allPage;
	}
	
	/**
	 * 获得总条数
	 * @return	总条数
	 */
	public int getAllCount() {
		return allCount;
	}
	
	/**
	 * 设置总条数
	 * @param allCount 总条数,>=0,否则无效
	 */
	public void setAllCount(int allCount) {
		if(allCount>=0){
			this.allCount = allCount;
			this.allPage = (this.allCount+this.pageCount-1)/this.pageCount;
		}		
	}
}