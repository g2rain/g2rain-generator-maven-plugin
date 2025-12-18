package com.g2rain.generator;

import com.g2rain.generator.config.FoundryConfig;
import com.g2rain.generator.generator.FoundryGenerator;

/**
 * G2rainGenerator测试类
 *
 * @author jagger
 * @since 2025/8/28-19:19
 */
public class G2rainGeneratorTest {

    /**
     * G2rainGenerator.generate测试方法
     */
//  @Test
    public void generate() throws Exception {
        FoundryConfig config = new FoundryConfig(
                null,
                "com.g2rain.generator.target",
                "jdbc:mysql://localhost:3306/g2rain?serverTimezone=UTC",
                "com.mysql.cj.jdbc.Driver",
                "root",
                "Sun_king18"
        );

        config.setStepIn(true);
        config.setTables("login_token");
        config.setOverwrite(false);
        new FoundryGenerator(null, config).generate();
    }
}
