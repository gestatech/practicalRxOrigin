package org.dogepool.practicalrx.controllers;

import java.util.List;

import org.dogepool.practicalrx.domain.UserStat;
import org.dogepool.practicalrx.services.CoinService;
import org.dogepool.practicalrx.services.ExchangeRateService;
import org.dogepool.practicalrx.services.HashrateService;
import org.dogepool.practicalrx.services.PoolService;
import org.dogepool.practicalrx.services.RankingService;
import org.dogepool.practicalrx.services.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A utility controller that displays the welcome message as HTML on root endpoint.
 */
@RestController
@RequestMapping("/")
public class IndexController {

    @Autowired
    private StatService statService;

    @Autowired
    private PoolService poolService;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        List<UserStat> hashLadder = statService.getLadderByHashrate();
        List<UserStat> coinsLadder = statService.getLadderByCoins();

        StringBuilder html = new StringBuilder("<html><body>");


        html.append("<h1>Welcome to " + poolService.poolName() + " dogecoin mining pool</h1>");
        html.append("<p>" + poolService.miningUsers().size() + " users currently mining, for a global hashrate of "
                + poolService.poolGigaHashrate() + " GHash/s</p>");

        html.append("<p>1 DOGE = " + exchangeRateService.dogeToCurrencyExchangeRate("USD") + "$<br/>");
        html.append("1 DOGE = " + exchangeRateService.dogeToCurrencyExchangeRate("EUR") + "€</p>");

        html.append("<p><h3>----- TOP 10 Miners by Hashrate -----</h3>");
        int rank = 1;
        for (UserStat userStat : hashLadder) {
            html.append("<br/>").append(rank++)
                    .append(": ").append(userStat.user.nickname)
                    .append(", ").append(userStat.hashrate).append(" GHash/s");
        }
        html.append("</p>");

        html.append("<p><h3>----- TOP 10 Miners by Coins Found -----</h3>");
        rank = 1;
        for (UserStat userStat : coinsLadder) {
            html.append("<br/>").append(rank++).append(": ")
                    .append(userStat.user.nickname).append(", ")
                    .append(userStat.totalCoinsMined).append(" dogecoins");
        }
        html.append("</p>");
        html.append("</body></html>");
        return html.toString();
    }
}