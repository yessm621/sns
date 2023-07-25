package com.me.sns.controller.response;

import com.me.sns.model.Alarm;
import com.me.sns.model.entity.AlarmArgs;
import com.me.sns.model.entity.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class AlarmResponse {

    private Integer id;
    private AlarmType alarmType;
    private AlarmArgs alarmArgs;
    private String text;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static AlarmResponse fromAlarm(Alarm alarm) {
        return new AlarmResponse(
                alarm.getId(),
                alarm.getAlarmType(),
                alarm.getArgs(),
                alarm.getAlarmType().getAlarmText(),
                alarm.getRegisteredAt(),
                alarm.getUpdatedAt(),
                alarm.getDeletedAt()
        );
    }
}
