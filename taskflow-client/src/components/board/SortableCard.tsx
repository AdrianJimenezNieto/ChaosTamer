import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import type { Card } from "../../models";

interface Props {
  card: Card;
}

export default function SortableCard({ card }: Props) {

  // Convert the ID into String for DND stability
  const cardId = String(card.id);

  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({
    id: cardId,
    data: { id: card.id, type:'Card' }
  });

  const style = {
    // CSS transform
    transform: CSS.Translate.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  }

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      className="rounded-md bg-gray-700 p-3 shadow-md text-white text-sm cursor-grab hover:bg-gray-600 active:cursor-grabbing touch-none mb-2"
    >
      {card.title}: {card.id}
    </div>
  )
}