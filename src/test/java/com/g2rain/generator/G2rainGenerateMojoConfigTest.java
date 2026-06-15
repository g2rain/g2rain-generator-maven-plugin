package com.g2rain.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class G2rainGenerateMojoConfigTest {

    @TempDir
    Path tempDir;

    @Test
    void propertiesCanOverrideDefaultIsolationSettings() throws Exception {
        G2rainGenerateMojo mojo = createMojoWithConfig("""
            project.basePackage=com.example.demo
            database.url=jdbc:mysql://localhost:3306/demo
            database.driver=com.mysql.cj.jdbc.Driver
            database.username=root
            database.password=pwd
            database.tables=user
            data.isolation.withIsolation=false
            data.isolation.tenantColumns=tenant_id,organ_id
            """);

        assertFalse(invokeResolveWithIsolation(mojo));
        assertEquals("tenant_id,organ_id", invokeResolveTenantColumns(mojo));
    }

    @Test
    void cliParametersTakePriorityOverProperties() throws Exception {
        G2rainGenerateMojo mojo = createMojoWithConfig("""
            project.basePackage=com.example.demo
            database.url=jdbc:mysql://localhost:3306/demo
            database.driver=com.mysql.cj.jdbc.Driver
            database.username=root
            database.password=pwd
            database.tables=user
            data.isolation.withIsolation=false
            data.isolation.tenantColumns=tenant_id,organ_id
            data.isolation.excludeTables=article
            """);

        setField(mojo, "withIsolation", Boolean.TRUE);
        setField(mojo, "tenantColumns", "organ_id");
        setField(mojo, "excludeTables", "dict_type");

        assertTrue(invokeResolveWithIsolation(mojo));
        assertEquals("organ_id", invokeResolveTenantColumns(mojo));
        assertEquals("dict_type", invokeResolveExcludeTables(mojo));
    }

    @Test
    void defaultsApplyWhenConfigAndCliAreMissing() throws Exception {
        G2rainGenerateMojo mojo = new G2rainGenerateMojo();

        assertTrue(invokeResolveWithIsolation(mojo));
        assertEquals("organ_id", invokeResolveTenantColumns(mojo));
        assertEquals("", invokeResolveExcludeTables(mojo));
    }

    private G2rainGenerateMojo createMojoWithConfig(String content) throws Exception {
        Path configPath = tempDir.resolve("codegen.properties");
        Files.writeString(configPath, content);

        G2rainGenerateMojo mojo = new G2rainGenerateMojo();
        setField(mojo, "configFile", configPath.toFile());

        Method loadConfig = G2rainGenerateMojo.class.getDeclaredMethod("loadFoundryConfigFile");
        loadConfig.setAccessible(true);
        loadConfig.invoke(mojo);
        return mojo;
    }

    private boolean invokeResolveWithIsolation(G2rainGenerateMojo mojo) throws Exception {
        Method method = G2rainGenerateMojo.class.getDeclaredMethod("resolveWithIsolation");
        method.setAccessible(true);
        return (boolean) method.invoke(mojo);
    }

    private String invokeResolveTenantColumns(G2rainGenerateMojo mojo) throws Exception {
        Method method = G2rainGenerateMojo.class.getDeclaredMethod("resolveTenantColumns");
        method.setAccessible(true);
        return (String) method.invoke(mojo);
    }

    private String invokeResolveExcludeTables(G2rainGenerateMojo mojo) throws Exception {
        Method method = G2rainGenerateMojo.class.getDeclaredMethod("resolveExcludeTables");
        method.setAccessible(true);
        return (String) method.invoke(mojo);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
