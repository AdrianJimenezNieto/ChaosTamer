package com.taskflow.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "task_lists")
@Data
public class TaskListEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "list_order")
  private Integer listOrder;
  
  // Relationships
  // Owned by a Board
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id", nullable = false)
  private BoardEntity board;

  // Owns Tasks
  @OneToMany(mappedBy="taskList", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("card_order ASC")
  private List<CardEntity> cards;
}
