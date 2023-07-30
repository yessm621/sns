package com.me.sns.service;

import com.me.sns.exception.ErrorCode;
import com.me.sns.exception.SnsApplicationException;
import com.me.sns.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String ALARM_NAME = "alarm";
    private final EmitterRepository emitterRepository;

    public void send(Integer alarmId, Integer userId) {
        emitterRepository.get(userId).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(sseEmitter.event().id(alarmId.toString()).name(ALARM_NAME).data("new alarm"));
            } catch (IOException e) {
                emitterRepository.delete(userId);
                throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter founded"));
    }

    public SseEmitter connectAlarm(Integer userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, sseEmitter);

        // 시간 초과, 네트워크 오류를 포함한 모든 이유로 비동기 요청이 정상 동작할 수 없을 때 저장해둔 SseEmitter를 삭제함.
        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        try {
            // 연결 요청에 의해 SseEmitter가 생성되면 더미 데이터를 보내줘야함.
            // 연결된 후 하나의 데이터도 전송되지 않는다면 SseEmitter의 유효시간이 끝났을 경우
            // 503 응답이 발생하므로 연결시 바로 더미 데이터를 한 번 보내준다.
            sseEmitter.send(SseEmitter.event().id("id").name(ALARM_NAME).data("connect complete"));
        } catch (IOException exception) {
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }
}
