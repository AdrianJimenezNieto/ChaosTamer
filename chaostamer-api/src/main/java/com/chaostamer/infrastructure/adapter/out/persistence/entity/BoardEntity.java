package com.chaostamer.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "boards")
@Data
public class BoardEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  // Relationships
  // Owner of the board
  @ManyToOne(fetch = FetchType.LAZY) // LAZY = dont load till needed
  @JoinColumn(name = "user_id", nullable = false) // Foreign key column
  private UserEntity owner;

  // Owns a TaskList
  @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("id ASC")
  private List<TaskListEntity> taskLists;
}
