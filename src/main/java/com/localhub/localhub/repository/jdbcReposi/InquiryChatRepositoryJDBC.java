package com.localhub.localhub.repository.jdbcReposi;

import com.localhub.localhub.dto.response.InquiryChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InquiryChatRepositoryJDBC {


    private final NamedParameterJdbcTemplate template;


    public List<InquiryChatDto> findByUserId(Long userId) {

        String sql = """
                SELECT
                                                 iq.id AS chatroom_id,
                
                                                 r.owner_id AS owner_id,
                                                 owner_u.name AS owner_name,
                
                                                 customer_u.id AS customer_id,
                                                 customer_u.name AS customer_name,
                
                                                 m.content AS last_message,
                                                 m.created_at AS last_message_at,
                
                                                 r.id AS restaurant_id
                
                                             FROM inquiry_chat iq
                
                                             -- 1. 내가 속한 채팅방
                                             JOIN user_chatroom_mapping ucm_me
                                               ON ucm_me.chatroom_id = iq.id
                                              AND ucm_me.user_id = :user_id
                
                                             -- 2. 채팅방의 레스토랑
                                             JOIN restaurant r
                                               ON r.id = iq.restaurant_id
                
                                             -- 3. 사장 정보
                                             JOIN users owner_u
                                               ON owner_u.id = r.owner_id
                
                                             -- 4. 사장이 아닌 참가자 = customer
                                             JOIN user_chatroom_mapping ucm_customer
                                               ON ucm_customer.chatroom_id = iq.id
                                              AND ucm_customer.user_id <> r.owner_id
                
                                             JOIN users customer_u
                                               ON customer_u.id = ucm_customer.user_id
                
                                             -- 마지막 메시지
                                             LEFT JOIN message m
                                               ON m.id = (
                                                 SELECT m2.id
                                                 FROM message m2
                                                 WHERE m2.chatroom_id = iq.id
                                                 ORDER BY m2.created_at DESC
                                                 LIMIT 1
                                               )
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);

        List<InquiryChatDto> result = template.query(sql, params, (rs, roNum) ->
                InquiryChatDto.builder()
                        .ownerId(rs.getLong("owner_id"))
                        .id(rs.getLong("chatroom_id"))
                        .restaurantId(rs.getLong("restaurant_id"))
                        .ownerName(rs.getString("owner_name"))
                        .lastMessage(rs.getString("last_message"))
                        .lastMessageTime(  Optional.ofNullable(rs.getTimestamp("last_message_at"))
                                .map(ts -> ts.toLocalDateTime())
                                .orElse(null))
                        .customerId(rs.getLong("customer_id"))
                        .customerName(rs.getString("customer_name"))
                        .build()

        ).stream().toList();
        return result;

    }
}
