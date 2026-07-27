package com.sentinel.threat.signatures;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class AttackSignatureRegistry {

    private final List<Pattern> sqlInjectionPatterns = List.of(
            Pattern.compile("(?i)(\\bunion\\b.*\\bselect\\b|'\\s*or\\s*'1'='1|'\\s*or\\s*1=1|--|;\\s*drop\\s+table|;\\s*delete\\s+from)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?i)(exec(\\s|\\+)+(s|x)p_|select.*from.*information_schema)", Pattern.CASE_INSENSITIVE)
    );

    private final List<Pattern> xssPatterns = List.of(
            Pattern.compile("(?i)(<script[^>]*>.*?</script>|javascript:|onerror\\s*=|onload\\s*=|eval\\s*\\()", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?i)(<iframe|document\\.cookie|<img[^>]+src\\s*=\\s*[\"']?javascript:)", Pattern.CASE_INSENSITIVE)
    );

    private final List<Pattern> pathTraversalPatterns = List.of(
            Pattern.compile("(\\.\\./|\\.\\.\\\\|%2e%2e%2f|%2e%2e/|\\.\\.%2f|%2e%2e%5c)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(/etc/passwd|/etc/shadow|c:\\\\boot\\.ini|\\\\system32\\\\)", Pattern.CASE_INSENSITIVE)
    );

    private final List<Pattern> commandInjectionPatterns = List.of(
            Pattern.compile("(?i)(;\\s*(cat|ls|whoami|id|nc|curl|wget|bash|sh|powershell|cmd)\\b|\\|\\s*(cat|ls|whoami|powershell|cmd))", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(`[^`]+`|\\$\\([^\\)]+\\))", Pattern.CASE_INSENSITIVE)
    );

    private final List<Pattern> rcePatterns = List.of(
            Pattern.compile("(?i)(Runtime\\.getRuntime\\(\\)\\.exec|ProcessBuilder|passthru\\(|shell_exec\\(|system\\(|unserialize\\()", Pattern.CASE_INSENSITIVE)
    );

    private final List<Pattern> botUserAgentPatterns = List.of(
            Pattern.compile("(?i)(sqlmap|nikto|nmap|masscan|zgrab|gobuster|dirbuster|python-requests|curl|wget|libwww-perl|censys)", Pattern.CASE_INSENSITIVE)
    );

    public String matchSqlInjection(String input) {
        return findFirstMatch(sqlInjectionPatterns, input);
    }

    public String matchXss(String input) {
        return findFirstMatch(xssPatterns, input);
    }

    public String matchPathTraversal(String input) {
        return findFirstMatch(pathTraversalPatterns, input);
    }

    public String matchCommandInjection(String input) {
        return findFirstMatch(commandInjectionPatterns, input);
    }

    public String matchRce(String input) {
        return findFirstMatch(rcePatterns, input);
    }

    public String matchBotUserAgent(String userAgent) {
        return findFirstMatch(botUserAgentPatterns, userAgent);
    }

    private String findFirstMatch(List<Pattern> patterns, String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                return matcher.group(0);
            }
        }
        return null;
    }
}
