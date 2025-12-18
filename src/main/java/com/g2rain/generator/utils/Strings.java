package com.g2rain.generator.utils;


import java.util.Objects;

/**
 * 字符串工具类，提供常用的字符串格式化与命名转换方法。
 * <p>
 * 本类主要用于：
 * <ul>
 *     <li>下划线命名与驼峰命名互转；</li>
 *     <li>首字母大小写转换；</li>
 *     <li>字符串空白判断。</li>
 * </ul>
 * <p>
 * 所有方法均为静态方法，可直接调用，无需实例化。
 *
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * Strings.underlineToCamel("user_name", false); // userName
 * Strings.underlineToCamel("user_name", true);  // UserName
 * Strings.firstCharToUpper("alpha");            // Alpha
 * Strings.firstCharToLower("Alpha");            // alpha
 * Strings.isBlank("   ");                       // true
 * }</pre>
 *
 * @author jagger
 * @since 2025/10/28
 */
public final class Strings {

    /**
     * 私有构造方法，防止实例化。
     */
    private Strings() {
        // 禁止实例化
    }

    /**
     * 将下划线命名（snake_case）转换为驼峰命名（camelCase 或 PascalCase）。
     *
     * <p>规则：
     * <ul>
     *     <li>以下划线分隔的每个单词首字母转换为大写；</li>
     *     <li>首单词根据参数 {@code firstCharUpper} 决定首字母是否大写；</li>
     *     <li>连续多个下划线会被视为单个分隔符；</li>
     *     <li>非字母字符不受影响。</li>
     * </ul>
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * Strings.underlineToCamel("user_name", false); // userName
     * Strings.underlineToCamel("user_name", true);  // UserName
     * Strings.underlineToCamel("user__info", false); // userInfo
     * }</pre>
     *
     * @param str            输入的下划线命名字符串
     * @param firstCharUpper 是否将首字母大写（true 表示生成 PascalCase）
     * @return 转换后的驼峰命名字符串；若输入为空或 null，则返回原值
     */
    public static String underlineToCamel(String str, boolean firstCharUpper) {
        if (Objects.isNull(str) || str.isEmpty()) {
            return str;
        }

        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;

        for (int i = 0, j = str.length(); i < j; i++) {
            char c = str.charAt(i);
            if (c == '_') {
                nextUpper = true;
                continue;
            }

            if (nextUpper) {
                sb.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }

        if (firstCharUpper && !sb.isEmpty()) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }

        return sb.toString();
    }

    /**
     * 将字符串首字母转换为小写。
     * <p>若字符串为空或 null，则返回原值。</p>
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * Strings.firstCharToLower("Alpha"); // alpha
     * }</pre>
     *
     * @param str 输入字符串
     * @return 首字母转小写后的字符串
     */
    public static String firstCharToLower(String str) {
        if (Objects.isNull(str) || str.isEmpty()) {
            return str;
        }

        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 将字符串首字母转换为大写。
     * <p>若字符串为空或 null，则返回原值。</p>
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * Strings.firstCharToUpper("alpha"); // Alpha
     * }</pre>
     *
     * @param str 输入字符串
     * @return 首字母转大写后的字符串
     */
    public static String firstCharToUpper(String str) {
        if (Objects.isNull(str) || str.isEmpty()) {
            return str;
        }

        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 判断字符串是否为空白。
     * <p>
     * 空白包括：
     * <ul>
     *     <li>{@code null}</li>
     *     <li>空字符串 {@code ""}</li>
     *     <li>仅包含空格、制表符、换行符等空白字符的字符串</li>
     * </ul>
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * Strings.isBlank(" ");  // true
     * Strings.isBlank(null); // true
     * Strings.isBlank("a");  // false
     * }</pre>
     *
     * @param str 输入字符串
     * @return 若字符串为 null 或仅包含空白字符则返回 true
     */
    public static boolean isBlank(String str) {
        return Objects.isNull(str) || str.isBlank();
    }

    /**
     * 判断字符串是否非空白。
     * <p>与 {@link #isBlank(String)} 相反。</p>
     *
     * @param str 输入字符串
     * @return 若字符串不为 null 且包含非空白字符，则返回 true
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}