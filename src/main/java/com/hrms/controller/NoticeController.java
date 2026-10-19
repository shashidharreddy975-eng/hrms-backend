
package com.hrms.controller;

import com.hrms.entity.Notice;
import com.hrms.repository.NoticeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeRepository noticeRepository;

    public NoticeController(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    // ADMIN and STAFF can view notices
    @GetMapping
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Notice getNoticeById(@PathVariable Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Notice not found: " + id));
    }

    // Only ADMIN can create notices
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Notice createNotice(@RequestBody Notice notice) {
        notice.setId(null);
        return noticeRepository.save(notice);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Notice updateNotice(
            @PathVariable Long id,
            @RequestBody Notice updatedNotice) {

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Notice not found: " + id));

        notice.setTitle(updatedNotice.getTitle());
        notice.setMessage(updatedNotice.getMessage());

        return noticeRepository.save(notice);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteNotice(@PathVariable Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new RuntimeException("Notice not found: " + id);
        }
        noticeRepository.deleteById(id);
    }
}
