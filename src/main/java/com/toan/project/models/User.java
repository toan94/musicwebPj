package com.toan.project.models;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(	name = "users", 
		uniqueConstraints = { 
			@UniqueConstraint(columnNames = "username"),
			@UniqueConstraint(columnNames = "email") 
		})
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long user_id;

	private String firebaseToken;

	private String topics;

	@ManyToMany(mappedBy = "buyers")
	private Set<Song> purchasedSong;

	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
	private Set<PushMessage> sentMessage;

	@NotBlank
	@Size(max = 20)
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	private Long coin = 0L;


	@NotBlank
	@Size(max = 120)
	private String password;

	private String AvatarLink;


	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_roles", 
				joinColumns = @JoinColumn(name = "user_id"), 
				inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();



	@OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
	private Set<Song> songs;

	@OneToMany(mappedBy = "OwnedUser", cascade = CascadeType.ALL)
	private Set<Playlist> ListOfPlaylist;

	public User() {
	}


	public String getTopics() {
		return topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public Set<Song> getPurchasedSong() {
		return purchasedSong;
	}

	public void setPurchasedSong(Set<Song> purchasedSong) {
		this.purchasedSong = purchasedSong;
	}

	public Long getCoin() {
		return coin;
	}

	public void setCoin(Long coin) {
		this.coin = coin;
	}

	public Set<PushMessage> getSentMessage() {
		return sentMessage;
	}

	public void setSentMessage(Set<PushMessage> sentMessage) {
		this.sentMessage = sentMessage;
	}

	public String getAvatarLink() {
		return AvatarLink;
	}

	public String getFirebaseToken() {
		return firebaseToken;
	}

	public void setFirebaseToken(String firebaseToken) {
		this.firebaseToken = firebaseToken;
	}

	public void setAvatarLink(String avatarLink) {
		AvatarLink = avatarLink;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Set<Playlist> getListOfPlaylist() {
		return ListOfPlaylist;
	}

	public void setListOfPlaylist(Set<Playlist> listOfPlaylist) {
		ListOfPlaylist = listOfPlaylist;
	}

	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}
	public Set<Song> getSongs() {
		return songs;
	}

	public void setSongs(Set<Song> songs) {
		this.songs = songs;
	}
	public Long getId() {
		return user_id;
	}

	public void setId(Long user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}
