package io.hhplus.javaconcerthancil.interfaces.api.v1.user.response;

public record UserBalanceResponse(
        long userId,
        long currentAmount
) {
}
