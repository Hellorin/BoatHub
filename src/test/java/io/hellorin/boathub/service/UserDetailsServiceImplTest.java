package io.hellorin.boathub.service;

import io.hellorin.boathub.domain.UserEntity;
import io.hellorin.boathub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserDetailsServiceImpl class.
 * Tests user loading functionality for Spring Security authentication.
 */
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl userDetailsService;

    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        // Initialize service with mocked repository
        userDetailsService = new UserDetailsServiceImpl(userRepository);
        
        // Create test user entity
        testUserEntity = new UserEntity();
        testUserEntity.setId(1L);
        testUserEntity.setUsername("testuser");
        testUserEntity.setPassword("encodedPassword123");
        testUserEntity.setEnabled(true);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUserEntity));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("encodedPassword123");
        assertThat(result.isEnabled()).isTrue();
        assertThat(result.isAccountNonExpired()).isTrue();
        assertThat(result.isCredentialsNonExpired()).isTrue();
        assertThat(result.isAccountNonLocked()).isTrue();
        
        // Verify authorities
        Collection<? extends GrantedAuthority> authorities = result.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority).contains("ROLE_USER");
        
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        // Given
        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: " + username);
        
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WhenUserIsDisabled_ShouldReturnDisabledUserDetails() {
        // Given
        String username = "disableduser";
        UserEntity disabledUser = new UserEntity();
        disabledUser.setId(2L);
        disabledUser.setUsername("disableduser");
        disabledUser.setPassword("encodedPassword123");
        disabledUser.setEnabled(false);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(disabledUser));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("disableduser");
        assertThat(result.getPassword()).isEqualTo("encodedPassword123");
        assertThat(result.isEnabled()).isFalse();
        assertThat(result.isAccountNonExpired()).isTrue();
        assertThat(result.isCredentialsNonExpired()).isTrue();
        assertThat(result.isAccountNonLocked()).isTrue();
        
        // Verify authorities are still present
        Collection<? extends GrantedAuthority> authorities = result.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority).contains("ROLE_USER");
        
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WhenUsernameIsNull_ShouldThrowUsernameNotFoundException() {
        // Given
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(null))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: null");
        
        verify(userRepository).findByUsername(null);
    }

    @Test
    void loadUserByUsername_WhenUsernameIsEmpty_ShouldThrowUsernameNotFoundException() {
        // Given
        String emptyUsername = "";
        when(userRepository.findByUsername(emptyUsername)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(emptyUsername))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: " + emptyUsername);
        
        verify(userRepository).findByUsername(emptyUsername);
    }

    @Test
    void loadUserByUsername_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenThrow(new RuntimeException("Database connection error"));

        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database connection error");
        
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WithDifferentUserData_ShouldReturnCorrectUserDetails() {
        // Given
        String username = "adminuser";
        UserEntity adminUser = new UserEntity();
        adminUser.setId(2L);
        adminUser.setUsername("adminuser");
        adminUser.setPassword("adminPassword456");
        adminUser.setEnabled(true);
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(adminUser));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("adminuser");
        assertThat(result.getPassword()).isEqualTo("adminPassword456");
        assertThat(result.isEnabled()).isTrue();
        assertThat(result.isAccountNonExpired()).isTrue();
        assertThat(result.isCredentialsNonExpired()).isTrue();
        assertThat(result.isAccountNonLocked()).isTrue();
        
        // Verify authorities
        Collection<? extends GrantedAuthority> authorities = result.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority).contains("ROLE_USER");
        
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WithSpecialCharactersInUsername_ShouldHandleCorrectly() {
        // Given
        String username = "user@domain.com";
        testUserEntity.setUsername("user@domain.com");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUserEntity));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("user@domain.com");
        assertThat(result.getPassword()).isEqualTo("encodedPassword123");
        assertThat(result.isEnabled()).isTrue();
        
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WithLongUsername_ShouldHandleCorrectly() {
        // Given
        String longUsername = "verylongusernamethatexceedsnormallengthlimits";
        testUserEntity.setUsername(longUsername);
        when(userRepository.findByUsername(longUsername)).thenReturn(Optional.of(testUserEntity));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(longUsername);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(longUsername);
        assertThat(result.getPassword()).isEqualTo("encodedPassword123");
        assertThat(result.isEnabled()).isTrue();
        
        verify(userRepository).findByUsername(longUsername);
    }

    @Test
    void loadUserByUsername_VerifyUserDetailsProperties_ShouldHaveCorrectValues() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUserEntity));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(result).isNotNull();
        
        // Verify all UserDetails properties
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("encodedPassword123");
        assertThat(result.isEnabled()).isTrue();
        assertThat(result.isAccountNonExpired()).isTrue();
        assertThat(result.isCredentialsNonExpired()).isTrue();
        assertThat(result.isAccountNonLocked()).isTrue();
        
        // Verify authorities contain only ROLE_USER
        Collection<? extends GrantedAuthority> authorities = result.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_USER");
        
        verify(userRepository).findByUsername(username);
    }


    @Test
    void loadUserByUsername_WithEmptyPassword_ShouldHandleCorrectly() {
        // Given
        String username = "testuser";
        testUserEntity.setPassword("");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUserEntity));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEmpty();
        assertThat(result.isEnabled()).isTrue();
        
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_VerifyRepositoryCallCount_ShouldCallOnce() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUserEntity));

        // When
        userDetailsService.loadUserByUsername(username);

        // Then
        verify(userRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void loadUserByUsername_WithWhitespaceUsername_ShouldHandleCorrectly() {
        // Given
        String usernameWithSpaces = "  testuser  ";
        when(userRepository.findByUsername(usernameWithSpaces)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(usernameWithSpaces))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: " + usernameWithSpaces);
        
        verify(userRepository).findByUsername(usernameWithSpaces);
    }
}
