package io.hhplus.javaconcerthancil.web.waitingqueue.response;

import java.time.LocalDateTime;

public record IssueTokenResponse(Long tokenId, LocalDateTime createdAt, LocalDateTime expiredAt) {
}
