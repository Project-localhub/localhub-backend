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


    public List<InquiryChatDto> findByUsername(Long userId) {

        String sql = """
                SELECT 
                iq.id AS chatroom_id,
                u.id AS owner_id,
                u.username AS owner_name,
                m.content AS last_message,
                m.created_at AS last_message_at,
                r.id AS restaurant_id
                
                FROM inquiry_chat iq
                
                JOIN user_chatroom_mapping ucm
                on ucm.chatroom_id = iq.id
                
                LEFT JOIN restaurant r
                on r.id = iq.restaurant_id 
                
                LEFT JOIN users u
                on u.id = r.owner_id
                
                LEFT JOIN message m
                on m.id = (
                 SELECT m2.id
                 FROM message m2
                 WHERE m2.chatroom_id = iq.id
                 ORDER BY m2.created_at DESC
                 LIMIT 1
                )
                
                WHERE ucm.user_id = :user_id
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
                        .build()

        ).stream().toList();
        return result;

    }
}
