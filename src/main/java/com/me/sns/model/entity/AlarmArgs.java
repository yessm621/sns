package com.me.sns.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmArgs {

    // 알림을 발생시킨 사람
    private Integer fromUserId;
    private Integer targetId;
}

// comment: 00씨가 새 코멘트를 작성했습니다 -> postId, commentId
// 00외 2명이 새 코멘트를 작성했습니다 -> commentId, commentId
