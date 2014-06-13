package cz.edukomplex.kosilka.client.helper;

import java.util.List;

public interface MyDataProvider<T> {
	
	List<T>	getList();
	void update();
}
