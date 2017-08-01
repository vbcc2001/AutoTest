package com.xxx.lfs.object;

import java.util.ArrayList;
import java.util.List;

public class ResponseData<E> {
	private Error error= new Error();
    private List<E> list =new ArrayList<E>();
	public Error getError() {
		return error;
	}
	public void setError(Error error) {
		this.error = error;
	}
	public List<E> getList() {
		return list;
	}
	public void setList(List<E> list) {
		this.list = list;
	}
}
