package ch0101;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Log4j2
public class Statement {

    public static void main(String[] args) {
        try {
            Performance hamlet = new Performance("hamlet", 55);
            Performance asLike = new Performance("as-like", 35);
            Performance othello = new Performance("othello", 40);
            Invoice invoice = new Invoice("BigCo", List.of(hamlet, asLike, othello));

            Map<String, Play> plays = new HashMap<>();
            plays.put("hamlet", new Play("Hamlet", "tragedy"));
            plays.put("as-like", new Play("As You Like It", "comedy"));
            plays.put("othello", new Play("Othello", "tragedy"));

            String result = statement(invoice, plays);
            log.info("result : \n{}", result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("main END");
    }

    private static String statement(Invoice invoice, Map<String, Play> plays) {
        int totalAmount = 0;
        int volumeCredits = 0;
        String result = String.format("청구 내역 (고객명: %s)\n", invoice.getCustomer());

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

        for (Performance perf : invoice.getPerformances()) {
            Play play = plays.get(perf.getPlayId());
            int thisAmount = anoijntFor(perf, play);

            // 포인트를 적립한다.
            volumeCredits += (int) Math.max(perf.getAudience() - 30, 0);
            // 희극 관객 5명마다 추가 포인트를 제공한다.
            if ("comedy".equals(play.getType())) {
                volumeCredits += (int) Math.floor(perf.getAudience() / 5);
            }

            // 청구 내역을 출력한다.
            result += String.format("  %s: %s (%d석)\n", play.getName(), format.format(thisAmount / 100.0), (int) perf.getAudience());
            totalAmount += thisAmount;
        }

        result += String.format("총액: %s\n", format.format(totalAmount / 100.0));
        result += String.format("적립 포인트: %d점\n", volumeCredits);
        return result;
    }

    private static int anoijntFor(Performance perf, Play play) { //값이 바뀌지 않는 변수는 매개변수로 전달
        int thisAmount = 0; // 변수를 초기화하는 코드

        switch (play.getType()) {
            case "tragedy": // 비극
                thisAmount = 40000;
                if (perf.getAudience() > 30) {
                    thisAmount += (int) (1000 * (perf.getAudience() - 30));
                }
                break;
            case "comedy": // 희극
                thisAmount = 30000;
                if (perf.getAudience() > 20) {
                    thisAmount = (int) (thisAmount + (10000 + 500 * (perf.getAudience() - 20)));
                }
                thisAmount = (int) (thisAmount + 300 * perf.getAudience());
                break;
            default:
                throw new IllegalArgumentException(String.format("알 수 없는 장르: %s", play.getType()));
        }
        return thisAmount; // 함수 안에서 값이 바뀌는 변수 반환
    }

    @Data
    private static class Play {
        String name;
        String type;

        public Play(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    @Data
    private static class Invoice {
        String customer;
        List<Performance> performance;

        public Invoice(String customer, List<Performance> performance) {
            this.customer = customer;
            this.performance = performance;
        }

        public List<Performance> getPerformances() {
            return performance;
        }

    }

    @Data
    private static class Performance {
        String playId;
        double audience;

        public Performance(String playId, int audience) {
            this.playId = playId;
            this.audience = audience;
        }
    }
}
