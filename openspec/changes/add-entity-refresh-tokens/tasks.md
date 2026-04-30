## 1. Create RefreshToken entity

- [ ] 1.1 Create `model/RefreshToken.java` with `@Entity`, `@Table(name = "refresh_tokens")`, and Lombok `@Data` / `@NoArgsConstructor` / `@Builder`
- [ ] 1.2 Add `id` field: `@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id`
- [ ] 1.3 Add `token` field: `@Column(nullable = false, unique = true) private String token` (VARCHAR UUID)
- [ ] 1.4 Add `usuario` field: `@ManyToOne @JoinColumn(name = "usuario_id", nullable = false) private Usuario usuario`
- [ ] 1.5 Add `expiresAt` field: `@Column(nullable = false) private Instant expiresAt`
- [ ] 1.6 Add `revoked` field: `@Column(nullable = false) private boolean revoked = false`
- [ ] 1.7 Add `createdAt` field: `@Column(nullable = false, updatable = false) private Instant createdAt` with `@PrePersist` to set it to `Instant.now()`

## 2. Create RefreshTokenRepository

- [ ] 2.1 Create `repository/RefreshTokenRepository.java` extending `JpaRepository<RefreshToken, Long>`
- [ ] 2.2 Add `Optional<RefreshToken> findByToken(String token)`
- [ ] 2.3 Add `void deleteByUsuario(Usuario usuario)` and annotate with `@Transactional`

## 3. Test

- [ ] 3.1 Create `repositories/RefreshTokenRepositoryTest.java` with `@DataJpaTest` and H2 in-memory DB
- [ ] 3.2 Test `findByToken` returns the correct token when it exists
- [ ] 3.3 Test `findByToken` returns empty `Optional` when token does not exist
- [ ] 3.4 Test `deleteByUsuario` removes all tokens for a given user and leaves others untouched
