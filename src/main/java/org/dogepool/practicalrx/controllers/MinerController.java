package org.dogepool.practicalrx.controllers;

import org.dogepool.practicalrx.domain.User;
import org.dogepool.practicalrx.services.PoolService;
import org.dogepool.practicalrx.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/miner")
public class MinerController {

    @Autowired
    private UserService userService;

    @Autowired
    private PoolService poolService;

    @RequestMapping(method = RequestMethod.POST, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public ResponseEntity<String> connect(@PathVariable("id") long id) {
        User user = userService.getUser(id);
        if (user != null) {
            poolService.connectUser(user);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("{\"error\": \"User not authenticated\"}", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.ALL_VALUE)
    public ResponseEntity<String> disconnect(@PathVariable("id") long id) {
        User user = userService.getUser(id);
        if (user != null) {
            poolService.disconnectUser(user);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("{\"error\": \"User not authenticated\"}", HttpStatus.NOT_FOUND);
        }
    }

}
