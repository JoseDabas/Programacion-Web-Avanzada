import { useState, useEffect } from 'react';
import { getAllBookings, cancelBooking } from '../../services/booking.services';
import { getPropertyById } from '../../services/property.services';
import { Loader2, CalendarX, CheckCircle, Clock, AlertTriangle, X, Search, Filter } from 'lucide-react';

export default function AdminBookings() {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [cancelModalOpen, setCancelModalOpen] = useState(false);
    const [bookingToCancel, setBookingToCancel] = useState(null);
    const [isCanceling, setIsCanceling] = useState(false);

    // Filtros
    const [filterEstado, setFilterEstado] = useState('TODOS');
    const [searchText, setSearchText] = useState('');

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setLoading(true);
        try {
            const data = await getAllBookings();

            // Enriquecer con datos de propiedad
            const enrichedBookings = await Promise.all(data.map(async (booking) => {
                try {
                    const propDetails = await getPropertyById(booking.propiedadId);
                    return { ...booking, propertyDetails: propDetails };
                } catch (e) {
                    return { ...booking, propertyDetails: { name: 'Propiedad no encontrada', image: '' } };
                }
            }));

            // Ordenar más recientes primero
            enrichedBookings.sort((a, b) => b.id - a.id);
            setBookings(enrichedBookings);
        } catch (error) {
            console.error("Error cargando reservas:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleOpenCancelModal = (id) => {
        setBookingToCancel(id);
        setCancelModalOpen(true);
    };

    const confirmCancel = async () => {
        if (!bookingToCancel) return;
        setIsCanceling(true);
        try {
            await cancelBooking(bookingToCancel);
            setBookings(bookings.map(b => b.id === bookingToCancel ? { ...b, estado: 'CANCELADO' } : b));
            setCancelModalOpen(false);
            setBookingToCancel(null);
        } catch (e) {
            alert("No se pudo cancelar la reserva.");
        } finally {
            setIsCanceling(false);
        }
    };

    // Aplicar filtros
    const filteredBookings = bookings.filter(b => {
        const matchEstado = filterEstado === 'TODOS' || b.estado === filterEstado;
        const text = searchText.toLowerCase();
        const matchSearch = !text
            || b.clienteId?.toLowerCase().includes(text)
            || b.propertyDetails?.name?.toLowerCase().includes(text)
            || String(b.id).includes(text);
        return matchEstado && matchSearch;
    });

    // Contadores rápidos
    const countByStatus = (estado) => bookings.filter(b => b.estado === estado).length;

    if (loading) {
        return (
            <div className="flex justify-center items-center py-32">
                <Loader2 className="animate-spin text-secondary" size={40} />
                <span className="ml-3 text-gray-500 font-medium">Cargando todas las reservas...</span>
            </div>
        );
    }

    return (
        <div className="max-w-6xl mx-auto space-y-6">
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                <h1 className="text-3xl font-bold text-gray-800">Gestión de Reservas</h1>
                <div className="flex items-center gap-3 text-sm">
                    <span className="bg-gray-100 text-gray-700 px-3 py-1.5 rounded-full font-bold">{bookings.length} total</span>
                    <span className="bg-green-100 text-green-700 px-3 py-1.5 rounded-full font-bold">{countByStatus('COMPLETADO')} completadas</span>
                    <span className="bg-yellow-100 text-yellow-700 px-3 py-1.5 rounded-full font-bold">{countByStatus('PENDIENTE')} pendientes</span>
                    <span className="bg-red-100 text-red-700 px-3 py-1.5 rounded-full font-bold">{countByStatus('CANCELADO')} canceladas</span>
                </div>
            </div>

            {/* Filtros */}
            <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex flex-col md:flex-row gap-4">
                <div className="relative flex-grow">
                    <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input
                        type="text"
                        placeholder="Buscar por cliente, propiedad o ID..."
                        value={searchText}
                        onChange={(e) => setSearchText(e.target.value)}
                        className="w-full pl-10 pr-4 py-2.5 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-secondary/30 text-sm"
                    />
                </div>
                <div className="flex items-center gap-2">
                    <Filter size={18} className="text-gray-400" />
                    {['TODOS', 'PENDIENTE', 'COMPLETADO', 'CANCELADO'].map((estado) => (
                        <button
                            key={estado}
                            onClick={() => setFilterEstado(estado)}
                            className={`px-3 py-2 rounded-lg text-sm font-semibold transition-colors ${
                                filterEstado === estado
                                    ? 'bg-secondary text-white shadow-sm'
                                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                            }`}
                        >
                            {estado === 'TODOS' ? 'Todos' : estado.charAt(0) + estado.slice(1).toLowerCase()}
                        </button>
                    ))}
                </div>
            </div>

            {/* Tabla de reservas */}
            {filteredBookings.length === 0 ? (
                <div className="text-center py-16 text-gray-500 bg-white rounded-2xl shadow-sm border border-gray-100">
                    <p className="text-lg">No se encontraron reservas con los filtros aplicados.</p>
                </div>
            ) : (
                <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
                    <div className="overflow-x-auto">
                        <table className="w-full text-sm">
                            <thead>
                                <tr className="bg-gray-50 border-b border-gray-200">
                                    <th className="text-left px-5 py-3.5 font-bold text-gray-600 uppercase tracking-wider text-xs">ID</th>
                                    <th className="text-left px-5 py-3.5 font-bold text-gray-600 uppercase tracking-wider text-xs">Propiedad</th>
                                    <th className="text-left px-5 py-3.5 font-bold text-gray-600 uppercase tracking-wider text-xs">Cliente</th>
                                    <th className="text-left px-5 py-3.5 font-bold text-gray-600 uppercase tracking-wider text-xs">Check-In</th>
                                    <th className="text-left px-5 py-3.5 font-bold text-gray-600 uppercase tracking-wider text-xs">Check-Out</th>
                                    <th className="text-left px-5 py-3.5 font-bold text-gray-600 uppercase tracking-wider text-xs">Total</th>
                                    <th className="text-left px-5 py-3.5 font-bold text-gray-600 uppercase tracking-wider text-xs">Estado</th>
                                    <th className="text-left px-5 py-3.5 font-bold text-gray-600 uppercase tracking-wider text-xs">Acciones</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100">
                                {filteredBookings.map((booking) => (
                                    <tr key={booking.id} className="hover:bg-gray-50/50 transition-colors">
                                        <td className="px-5 py-4">
                                            <span className="font-bold text-gray-700">#{booking.id}</span>
                                        </td>
                                        <td className="px-5 py-4">
                                            <div className="flex items-center gap-3">
                                                {booking.propertyDetails?.image && (
                                                    <img
                                                        src={booking.propertyDetails.image}
                                                        alt=""
                                                        className="w-10 h-10 rounded-lg object-cover flex-shrink-0"
                                                    />
                                                )}
                                                <span className="font-medium text-gray-800 line-clamp-1 max-w-[180px]">
                                                    {booking.propertyDetails?.name || 'N/A'}
                                                </span>
                                            </div>
                                        </td>
                                        <td className="px-5 py-4 text-gray-600 max-w-[180px] truncate" title={booking.clienteId}>
                                            {booking.clienteId}
                                        </td>
                                        <td className="px-5 py-4 text-gray-600 whitespace-nowrap">{booking.fechaInicio}</td>
                                        <td className="px-5 py-4 text-gray-600 whitespace-nowrap">{booking.fechaFin}</td>
                                        <td className="px-5 py-4 font-bold text-gray-800 whitespace-nowrap">
                                            ${booking.totalPagar?.toFixed(2)}
                                        </td>
                                        <td className="px-5 py-4">
                                            {booking.estado === 'COMPLETADO' && (
                                                <span className="inline-flex items-center gap-1 text-xs font-bold text-green-700 bg-green-100 px-2.5 py-1 rounded-full">
                                                    <CheckCircle size={12} /> Completado
                                                </span>
                                            )}
                                            {booking.estado === 'PENDIENTE' && (
                                                <span className="inline-flex items-center gap-1 text-xs font-bold text-yellow-700 bg-yellow-100 px-2.5 py-1 rounded-full">
                                                    <Clock size={12} /> Pendiente
                                                </span>
                                            )}
                                            {booking.estado === 'CANCELADO' && (
                                                <span className="inline-flex items-center gap-1 text-xs font-bold text-red-700 bg-red-100 px-2.5 py-1 rounded-full">
                                                    <CalendarX size={12} /> Cancelado
                                                </span>
                                            )}
                                        </td>
                                        <td className="px-5 py-4">
                                            {(booking.estado === 'PENDIENTE' || booking.estado === 'COMPLETADO') && (
                                                <button
                                                    onClick={() => handleOpenCancelModal(booking.id)}
                                                    className="text-red-500 hover:text-white hover:bg-red-500 border border-red-300 font-semibold px-3 py-1.5 rounded-lg transition-colors text-xs"
                                                >
                                                    Cancelar
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Cancel Modal */}
            {cancelModalOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm animate-in fade-in duration-200">
                    <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6 animate-in zoom-in-95 duration-200">
                        <div className="flex justify-between items-start mb-4">
                            <div className="bg-red-100 p-3 rounded-full flex-shrink-0">
                                <AlertTriangle className="text-red-600" size={24} />
                            </div>
                            <button
                                onClick={() => setCancelModalOpen(false)}
                                className="text-gray-400 hover:text-gray-600 transition-colors"
                            >
                                <X size={20} />
                            </button>
                        </div>
                        <h3 className="text-xl font-bold text-gray-800 mb-2">¿Cancelar Reserva #{bookingToCancel}?</h3>
                        <p className="text-gray-500 mb-6">
                            Estás a punto de cancelar esta reserva como administrador. El cliente será notificado del cambio de estado.
                        </p>
                        <div className="flex gap-3 justify-end mt-2">
                            <button
                                onClick={() => setCancelModalOpen(false)}
                                disabled={isCanceling}
                                className="px-5 py-2 font-semibold text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors disabled:opacity-50"
                            >
                                Mantener
                            </button>
                            <button
                                onClick={confirmCancel}
                                disabled={isCanceling}
                                className="flex items-center gap-2 px-5 py-2 font-semibold text-white bg-red-600 rounded-lg hover:bg-red-700 transition-colors shadow-sm shadow-red-600/30 disabled:opacity-70"
                            >
                                {isCanceling && <Loader2 size={16} className="animate-spin" />}
                                Sí, Cancelar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
