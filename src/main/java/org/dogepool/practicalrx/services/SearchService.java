package org.dogepool.practicalrx.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.dogepool.practicalrx.domain.User;
import org.dogepool.practicalrx.domain.UserStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service to search for users by name, login and other criteria.
 */
@Service
public class SearchService {

    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;

    /**
     * Find users whose display name contain the given namePattern.
     *
     * @param namePattern the string to find in a user's displayName for him to match (ignoring case).
     * @return the list of matching users.
     */
    public List<User> findByName(String namePattern) {
        String upperPattern = namePattern.toUpperCase();
        List<User> result = new ArrayList<>();
        for (User u : userService.findAll()) {
            if (u.displayName.toUpperCase().contains(upperPattern)) {
                result.add(u);
            }
        }
        return result;
    }

    /**
     * Find users according to their number of coins found.
     *
     * @param minCoins the minimum number of coins found by a user for it to match.
     * @param maxCoins the maximum number of coins above which a user won't be considered a match. -1 to ignore.
     * @return the list of matching users.
     */
    public List<UserStat> findByCoins(long minCoins, long maxCoins) {

        List<User> allUsers = userService.findAll();
        int userListSize = allUsers.size();
        CountDownLatch latch = new CountDownLatch(userListSize);
        final List<UserStat> result = Collections.synchronizedList(new ArrayList<>(userListSize));
        for (User user : allUsers) {
            coinService.totalCoinsMinedBy(user, new ServiceCallback<Long>() {
                @Override
                public void onSuccess(Long coins) {
                    if (coins >= minCoins && (maxCoins < 0 || coins <= maxCoins)) {
                        result.add(new UserStat(user, -1d, coins));
                    }
                    latch.countDown();
                }
            });

        }
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
