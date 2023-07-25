package com.me.sns.repository;

import com.me.sns.model.entity.LikeEntity;
import com.me.sns.model.entity.PostEntity;
import com.me.sns.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {

    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);

    /*@Query(value = "SELECT COUNT(*) FROM LikeEntity entity WHERE entity.post =:post")
    Integer countByPost(@Param("post") PostEntity post);*/

    /**
     * TODO: @Query()를 사용하지 않고 JPA에서 제공하는 기본 함수를 사용하도록 변경함.
     */
    long countByPost(PostEntity post);

    /**
     * TODO: delete를 할 때는 JPA에 있는 기본 delete를 사용하는 것보다 @Query()를 통해 삭제하도록 변경함.
     */
    @Transactional
    @Modifying
    @Query("UPDATE LikeEntity entity SET deleted_at = NOW() where entity.post = :post")
    void deleteAllByPost(@Param("post") PostEntity postEntity);
}
