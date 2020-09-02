package test;

import com.alibaba.fastjson.JSONObject;
import com.dominic.base.batis.BaseBatis;
import com.dominic.base.batis.BaseBatisTestApplication;
import com.dominic.base.batis.dal.dao.BaseDao;
import com.dominic.base.batis.dal.entity.Product;
import com.dominic.base.batis.dal.sql.build.SelectParam;
import com.dominic.base.batis.dal.sql.build.UpdateParam;
import com.google.common.collect.Lists;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BaseBatisTestApplication.class)
public class ProductBaseDaoTest extends TestCase {

	private BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);

	@Test
	public void testSaveBatch() {
		Product data = new Product();
		data.setProductNo("P006");
		data.setProductName("1006");
		data.setPrice(BigDecimal.valueOf(1));
		data.setState(1);

		Product data2 = new Product();
		data2.setProductNo("P007");
		data2.setProductName("1007");
		data2.setPrice(BigDecimal.valueOf(1.1));
		data2.setState(1);

		ArrayList<Product> products = Lists.newArrayList(data, data2);
		productBaseDao.saveBatch(products, "productId");
		System.out.println(String.format("productId auto generated: %d, %d", data.getProductId(), data2.getProductId()));
	}

	@Test
	public void testSave() {
		Product data = new Product();
		data.setProductNo("P005");
		data.setProductName("1005");
		data.setPrice(BigDecimal.valueOf(0.8));
		data.setState(1);
		productBaseDao.save(data, "productId");
		System.out.println("productId auto generated: " + data.getProductId());
	}

	@Test
	public void testInsertBatch() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		Product data = new Product();
		data.setProductId(2L);
		data.setProductNo("P002");
		data.setProductName("1002");
		data.setPrice(BigDecimal.valueOf(0.5));
		data.setState(1);

		Product data2 = new Product();
		data2.setProductId(3L);
		data2.setProductNo("P003");
		data2.setProductName("1003");
		data2.setPrice(BigDecimal.valueOf(0.6));
		data2.setState(1);

		ArrayList<Product> products = Lists.newArrayList(data, data2);
		int result = productBaseDao.insertBatch(products);
		System.out.println(JSONObject.toJSONString(result));
	}

	@Test
	public void testDeleteById() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		int result = productBaseDao.deleteById("product_id", 2L);
		System.out.println(JSONObject.toJSONString(result));

		productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		int result2 = productBaseDao.deleteById("product_id",
				Lists.newArrayList(3L, 4L));
		System.out.println(JSONObject.toJSONString(result2));
	}

	@Test
	public void testInsert() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		Product data = new Product();
		data.setProductId(4L);
		data.setProductNo("P004");
		data.setProductName("1004");
		data.setPrice(BigDecimal.valueOf(0.7));
		data.setState(1);
		int result = productBaseDao.insert(data);
		System.out.println(JSONObject.toJSONString(result));

//		testInsertBatch();
	}

	@Test
	public void testUpdate() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		Product updateData = new Product();
		updateData.setCreateTime(new Date());
		UpdateParam updateParam = UpdateParam.builder().set("state", "2").set("price", null)
				.eq("product_no", "P003")
				.build();
		Product where = new Product();
		where.setProductName("1003");
		int update = productBaseDao.update(updateData, where, updateParam);
		System.out.println(JSONObject.toJSONString(update));
	}

	@Test
	public void testSelectById() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		Product record = productBaseDao.selectById("product_id", 1L);
		System.out.println(JSONObject.toJSONString(record));
	}

	@Test
	public void testSelectCount() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		SelectParam build = SelectParam.select()
				.like("product_name", "100")
				.in("state", Lists.newArrayList(1,2,3))
				.pageInfo(1, 10)
				.build();
		long count = productBaseDao.selectCount(build);
		System.out.println("total:" + count);
	}

	@Test
	public void testSelectList() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		Product where = new Product();
		where.setProductId(1L);
		SelectParam build = SelectParam.select()
				.like("product_name", "100")
//				.in("state", Lists.newArrayList(1,2,3))
				.pageInfo(1, 10)
				.build();
		List<Product> recordList = productBaseDao.selectList(where, build);
		System.out.println(JSONObject.toJSONString(recordList));
	}

	@Test
	public void testSelectOne3() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		SelectParam build = SelectParam.select()
				.eq("product_no", "P123456")
				.like("product_name", "1001")
				.in("state", Lists.newArrayList(1,2,3))
				.build();
		Product record = productBaseDao.selectOne(build);
		System.out.println(JSONObject.toJSONString(record));
	}

	@Test
	public void testSelectOne2() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		Product where = new Product();
		where.setProductId(1L);
		SelectParam build = SelectParam.select()
				.eq("product_no", "P123456")
				.in("state", Lists.newArrayList(1,2,3))
				.build();
		Product record = productBaseDao.selectOne(where, build);
		System.out.println(JSONObject.toJSONString(record));
	}

	@Test
	public void testSelectOne() {
		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		Product where = new Product();
		where.setProductId(1L);
		Product record = productBaseDao.selectOne(where);
		System.out.println(JSONObject.toJSONString(record));
	}

}
