## 1. Create RefreshToken entity

- [x] 1.1 Create `model/RefreshToken.java` with `@Entity`, `@Table(name = "refresh_tokens")`, and Lombok `@Data` / `@NoArgsConstructor` / `@Builder`
- [x] 1.2 Add `id` field: `@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id`
- [x] 1.3 Add `token` field: `@Column(nullable = false, unique = true) private String token` (VARCHAR UUID)
- [x] 1.4 Add `usuario` field: `@ManyToOne @JoinColumn(name = "usuario_id", nullable = false) private Usuario usuario`
- [x] 1.5 Add `expiresAt` field: `@Column(nullable = false) private Instant expiresAt`
- [x] 1.6 Add `revoked` field: `@Column(nullable = false) private boolean revoked = false`
- [x] 1.7 Add `createdAt` field: `@Column(nullable = false, updatable = false) private Instant createdAt` with `@PrePersist` to set it to `Instant.now()`

## 2. Create RefreshTokenRepository

- [x] 2.1 Create `repository/RefreshTokenRepository.java` extending `JpaRepository<RefreshToken, Long>`
- [x] 2.2 Add `Optional<RefreshToken> findByToken(String token)`
- [x] 2.3 Add `void deleteByUsuario(Usuario usuario)` and annotate with `@Transactional`

## 3. Test

- [x] 3.1 Create `repositories/RefreshTokenRepositoryTest.java` with `@DataJpaTest` and H2 in-memory DB
- [x] 3.2 Test `findByToken` returns the correct token when it exists
- [x] 3.3 Test `findByToken` returns empty `Optional` when token does not exist
- [x] 3.4 Test `deleteByUsuario` removes all tokens for a given user and leaves others untouched
