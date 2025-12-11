import React, { useState, useEffect } from "react";
import type { Card } from "../../models";

interface CardDetailModalProps {
    isOpen: boolean;
    onClose: () => void;
    card: Card | null;
    onSave: (cardId: number, newTitle: string, newDescription: string) => Promise<void>;
}

export const CardDetailModal: React.FC<CardDetailModalProps> = ({ isOpen, onClose, card, onSave }) => {
    // Local state of the form
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    // Sync the state when the selected card changes
    useEffect(() => {
        if (card) {
            setTitle(card.title);
            setDescription(card.description || '');
        }
    }, [card]);

    if (!isOpen || !card) return null;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!title.trim()) return; // Basic validation

        setIsLoading(true);
        try {
            await onSave(card.id, title, description);
            onClose(); // close the modal when finish
        } catch(error) {
            // TODO: Log visual errors
        } finally {
            setIsLoading(false);
        }
    };

    return (
        // Overlay
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 backfrop-blur-sm" onClick={onClose}>

            {/* Modal Content - Stop propagation for not closing the modal when clicking in it */}
            <div
                className="rounded-lg text-white bg-gray-800 shadow-xl w-full max-w-lg p-6 m-4"
                onClick={(e) => e.stopPropagation()}
            >
                <h2 className="text-xl font-bold mb-4">Editar Tarjeta</h2>

                <form onSubmit={handleSubmit}>
                    {/* Title */}
                    <div className="mb-4">
                        <label className="block text-sm font-medium mb-1">Título</label>
                        <input
                            type="text" 
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            className="w-full border border-gray-300 text-gray-900 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder:text-gray-600"
                            placeholder="Título de la tarjeta"
                            required
                        />
                    </div>

                    {/* Description */}
                    <div className="mb-6">
                        <label className="block text-sm font-medium mb-1">Descripción</label>
                        <textarea
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            className="w-full border border-gray-300 text-gray-900 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder:text-gray-600"
                            placeholder="Añade una descripción a la tarjeta..."
                        />
                    </div>

                    {/* Buttons */}
                    <div className="flex justify-end gap-3">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded transition-colors"
                            disabled={isLoading}
                        >
                            Cancelar
                        </button>

                        <button
                            type="submit"
                            className="px-4 py-2 bg-blue-600 text-white hover:bg-blue-700 rounded transition-colors disabled:opacity-50"
                            disabled={isLoading}
                        >
                            { isLoading ? 'Guardando...' : 'Guardar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}