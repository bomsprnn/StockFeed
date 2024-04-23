package com.example.stockfeed.Controller;

import com.example.stockfeed.Domain.NewsFeed;
import com.example.stockfeed.Dto.NewsFeedDto;
import com.example.stockfeed.Service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NewsFeedController {

    private final NewsFeedService newsFeedService;
    @GetMapping("/newsfeeds")
    public ResponseEntity<List<NewsFeedDto>> getNewsFeeds(@RequestParam Long userId, @RequestParam(required = false) Long cursorId, @RequestParam(defaultValue = "10") int size) {
        List<NewsFeedDto> newsFeeds = newsFeedService.getNewsFeedsWithCursor(userId, cursorId, size);
        return ResponseEntity.ok(newsFeeds);
    }
}
