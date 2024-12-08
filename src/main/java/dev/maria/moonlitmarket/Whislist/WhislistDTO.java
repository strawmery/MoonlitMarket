package dev.maria.moonlitmarket.Whislist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WhislistDTO {
    private Long id;
    private Long userId;
    private Long productId;
}
