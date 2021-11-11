package test;

import com.alibaba.fastjson.JSONObject;
import com.dominic.base.batis.BaseBatis;
import com.dominic.base.batis.BaseBatisTestApplication;
import com.dominic.base.batis.generator.dao.BaseDao;
import com.dominic.base.batis.entity.Product;
import com.dominic.base.batis.sql.build.SelectParam;
import com.dominic.base.batis.sql.build.UpdateParam;
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
public class ProductBaseDaoSpringTest extends TestCase {

	private BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);

	@Test
	public void testSaveBatch() {
		Product data = new Product();
		data.setProductNo("P116");
		data.setProductName("1116");
		data.setPrice(BigDecimal.valueOf(1));
		data.setState(1);

		Product data2 = new Product();
		data2.setProductNo("P117");
		data2.setProductName("1117");
		data2.setPrice(BigDecimal.valueOf(1.1));
		data2.setState(1);

		ArrayList<Product> products = Lists.newArrayList(data, data2);
//		productBaseDao.saveBatch(products, "productId");
		productBaseDao.saveBatch(products);
		System.out.printf("productId auto generated: %d, %d%n", data.getProductId(), data2.getProductId());
	}

	@Test
	public void testSave() {
		Product data = new Product();
		data.setProductNo("P118");
		data.setProductName("1118");
		data.setPrice(BigDecimal.valueOf(0.8));
		data.setState(1);
		productBaseDao.save(data);
		System.out.println("id auto generated: " + data.getProductId());
	}

	@Test
	public void testSave2() {
		Product data = new Product();
		data.setProductNo("P115");
		data.setProductName("1115");
		data.setPrice(BigDecimal.valueOf(0.8));
		data.setState(1);
		productBaseDao.save(data, "productId");
		System.out.println("productId auto generated: " + data.getProductId());

		data.setProductId(null);
		productBaseDao.save(data, "productId");
		System.out.println("productId auto generated: " + data.getProductId());
	}

//	@Autowired
//    private ProductDao productDao;
//	@Test
//	public void testSave3() {
//		Product data = new Product();
//		data.setProductNo("P115");
//		data.setProductName("1115");
//		data.setPrice(BigDecimal.valueOf(0.8));
//		data.setState(4);
//        productDao.save(data);
//		System.out.println("productId auto generated: " + data.getProductId());
//
//        productDao.save(data);
//		System.out.println("productId auto generated: " + data.getProductId());
//	}

	@Test
	public void testInsertBatch() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		Product data = new Product();
		data.setProductId(2L);
		data.setProductNo("P112");
		data.setProductName("1112");
		data.setPrice(BigDecimal.valueOf(0.5));
		data.setState(1);

		Product data2 = new Product();
		data2.setProductId(3L);
		data2.setProductNo("P113");
		data2.setProductName("1113");
		data2.setPrice(BigDecimal.valueOf(0.6));
		data2.setState(1);

		ArrayList<Product> products = Lists.newArrayList(data, data2);
		int result = productBaseDao.insertBatch(products);
		System.out.println(JSONObject.toJSONString(result));
	}

	@Test
	public void testDeleteById() {
		int result = productBaseDao.deleteById( 9L);
		System.out.println(result);

		result = productBaseDao.deleteById( Lists.newArrayList(7L, 8L));
		System.out.println(result);
	}

	@Test
	public void testDeleteById2() {
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
		data.setProductId(1105L);
		data.setProductNo("P115");
		data.setProductName("1115");
		data.setPrice(BigDecimal.valueOf(0.7));
		data.setState(1);
		int result = productBaseDao.insert(data);
		System.out.println(JSONObject.toJSONString(result));

//		testInsertBatch();
	}

	@Test
	public void testUpdateById() {
		Product updateData = new Product();
		updateData.setState(3);
		int update = productBaseDao.updateById(updateData, 4L);
//		int update = productBaseDao.updateById(updateData, "product_id", 12L);
//		int update = productBaseDao.updateById(updateData, Lists.newArrayList(10L, 11L));
//		int update = productBaseDao.updateById(updateData, "product_id", Lists.newArrayList(2L, 3L));
		System.out.println(update);
	}

	@Test
	public void testUpdate() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		Product updateData = new Product();
		updateData.setCreateTime(new Date());
		UpdateParam updateParam = UpdateParam.builder().set("state", "2").set("price", null)
				.eq("product_no", "P113")
				.build();
		Product where = new Product();
		where.setProductName("1113");
		int update = productBaseDao.update(updateData, where, updateParam);
		System.out.println(JSONObject.toJSONString(update));
	}

	@Test
	public void testSelectById() {
		Product record = productBaseDao.selectById( 1L);
		System.out.println(JSONObject.toJSONString(record));

		List<Product> productList = productBaseDao.selectById(Lists.newArrayList(1L, 2L));
		System.out.println(JSONObject.toJSONString(productList));
	}

	@Test
	public void testSelectById2() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		Product record = productBaseDao.selectById("product_id", 1L);
		System.out.println(JSONObject.toJSONString(record));
	}

	@Test
	public void testSelectCount() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		SelectParam build = SelectParam.where()
				.like("product_name", "111")
				.in("state", Lists.newArrayList(1,2,3))
				.page(1, 10)
				.build();
		long count = productBaseDao.selectCount(build);
		System.out.println("total:" + count);
	}

	@Test
	public void testSelectList() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		Product where = new Product();
		where.setProductId(1L);
		SelectParam build = SelectParam.where()
				.like("product_name", "111")
//				.in("state", Lists.newArrayList(1,2,3))
				.page(1, 10)
				.build();
		List<Product> recordList = productBaseDao.selectList(where, build);
		System.out.println(JSONObject.toJSONString(recordList));
	}

	@Test
	public void testSelectOne3() {
//		BaseDao<Product> productBaseDao = BaseBatis.getBaseDao("product", Product.class);
		SelectParam build = SelectParam.where()
				.eq("product_no", "P123456")
				.like("product_name", "1111")
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
		SelectParam build = SelectParam.where()
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
