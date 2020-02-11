package ming.zhang.boot_hbase.service;

import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author merz
 * @Description:
 */
@SpringBootTest
class IHbaseTemplateServiceTest {

    @Autowired
    private IHbaseTemplateService iHbaseTemplateService;

    @Test
    void createTable() {
        iHbaseTemplateService.createTable("test2", "info");
    }

    @Test
    void searchAll() {
        List<Per> test = iHbaseTemplateService.searchAll("test2", Per.class);
        System.out.println(test);
    }

    @Test
    void createPro() {
        Per per = new Per();
        per.setName("Merz");
        per.setAge("26");
        iHbaseTemplateService.createPro(per, "test2", "info", "1");
    }

    @Test
    void getOneToClass() {
        Per test2 = iHbaseTemplateService.getOneToClass(Per.class, "test2", "1");
        System.out.println(test2);
    }

    /**
     * MUST_PASS_ALL [and] {@link FilterList.Operator#MUST_PASS_ALL}
     * MUST_PASS_ONE [or] {@link FilterList.Operator#MUST_PASS_ONE}
     */
    @Test
    void getListByCondition() {
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        filterList.addFilter(new FamilyFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("info"))));
        List<Per> test2 = iHbaseTemplateService.getListByCondition(Per.class, "test2", filterList);
        System.out.println(test2);
    }

    @Test
    void getOneToMap() {
        Map<String, Object> test2 = iHbaseTemplateService.getOneToMap("test2", "1");
        System.out.println(test2);
    }

    @Test
    void getColumn() {
        String column = iHbaseTemplateService.getColumn("test2", "1", "info", "name");
        System.out.println(column);
    }

    @Test
    void findByRowRange() {
        List<Per> test2 = iHbaseTemplateService.findByRowRange(Per.class, "test2", "1", "3");
        System.out.println(test2);
    }

    @Test
    void searchAllByFilter() {
    }
}
