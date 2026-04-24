import { useState, useEffect } from 'react';
import { getMyBookings, cancelBooking, updateBooking, captureBookingPayment } from '../../services/booking.services';
import { getPropertyById } from '../../services/property.services';
import { PayPalScriptProvider, PayPalButtons } from "@paypal/react-paypal-js";
import { Loader2, CalendarX, CheckCircle, Clock, AlertTriangle, X, Pencil, Calendar, CreditCard } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function MyBookings() {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [cancelModalOpen, setCancelModalOpen] = useState(false);
    const [bookingToCancel, setBookingToCancel] = useState(null);
    const [isCanceling, setIsCanceling] = useState(false);

    // Edit modal states
    const [editModalOpen, setEditModalOpen] = useState(false);
    const [editBooking, setEditBooking] = useState(null);
    const [editCheckIn, setEditCheckIn] = useState('');
    const [editCheckOut, setEditCheckOut] = useState('');
    const [editError, setEditError] = useState('');
    const [isSavingEdit, setIsSavingEdit] = useState(false);

    // Pay modal states
    const [payModalOpen, setPayModalOpen] = useState(false);
    const [bookingToPay, setBookingToPay] = useState(null);
    const [payError, setPayError] = useState('');

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setLoading(true);
        try {
            const userStr = localStorage.getItem('user');
            if (userStr) {
                const user = JSON.parse(userStr);
                const data = await getMyBookings(user.email); // O user.id dependendiendo de la sesion

                // Las reservas no traen datos de la propiedad (nombre/imagen). Hacemos fetch a catalog-service para cada una.
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
            }
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

    const handleOpenPayModal = (booking) => {
        setBookingToPay(booking);
        setPayError('');
        setPayModalOpen(true);
    };

    const handlePaymentSuccess = async (data) => {
        try {
            await captureBookingPayment(bookingToPay.id, data.orderID);
            setBookings(bookings.map(b => b.id === bookingToPay.id ? { ...b, estado: 'COMPLETADO' } : b));
            setPayModalOpen(false);
            setBookingToPay(null);
        } catch (error) {
            console.error("Error confirmando el pago:", error);
            setPayError("El pago fue recibido por PayPal pero hubo un error de validación en el servidor.");
        }
    };

    // Edit dates
    const handleOpenEditModal = (booking) => {
        setEditBooking(booking);
        setEditCheckIn(booking.fechaInicio);
        setEditCheckOut(booking.fechaFin);
        setEditError('');
        setEditModalOpen(true);
    };

    const confirmEdit = async () => {
        if (!editBooking) return;
        if (!editCheckIn || !editCheckOut) {
            setEditError('Selecciona ambas fechas.');
            return;
        }
        if (new Date(editCheckIn) >= new Date(editCheckOut)) {
            setEditError('La fecha de salida debe ser posterior a la de entrada.');
            return;
        }

        setIsSavingEdit(true);
        setEditError('');

        // Recalcular total basado en las nuevas noches
        const nights = Math.ceil((new Date(editCheckOut) - new Date(editCheckIn)) / (1000 * 3600 * 24));
        const pricePerNight = editBooking.propertyDetails?.price || 0;
        const subtotal = pricePerNight * nights;
        const total = subtotal + (subtotal * 0.18);

        try {
            const updated = await updateBooking(editBooking.id, {
                fechaInicio: editCheckIn,
                fechaFin: editCheckOut,
                totalPagar: total
            });
            // Actualizar localmente
            setBookings(bookings.map(b => b.id === editBooking.id
                ? { ...b, fechaInicio: updated.fechaInicio, fechaFin: updated.fechaFin, totalPagar: updated.totalPagar }
                : b
            ));
            setEditModalOpen(false);
            setEditBooking(null);
        } catch (e) {
            setEditError(e.response?.data?.error || 'No se pudieron actualizar las fechas. Verifica disponibilidad.');
        } finally {
            setIsSavingEdit(false);
        }
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center py-32">
                <Loader2 className="animate-spin text-secondary" size={40} />
                <span className="ml-3 text-gray-500 font-medium">Cargando tus reservas...</span>
            </div>
        );
    }

    return (
        <div className="max-w-5xl mx-auto space-y-8">
            <h1 className="text-3xl font-bold text-gray-800 border-b pb-4">Mis Reservas</h1>

            {bookings.length === 0 ? (
                <div className="text-center py-20 text-gray-500 bg-white rounded-2xl shadow-sm border border-gray-100">
                    <p className="text-lg">No tienes ninguna reserva registrada.</p>
                    <Link to="/" className="text-secondary hover:underline mt-2 inline-block font-semibold">Explorar propiedades</Link>
                </div>
            ) : (
                <div className="space-y-6">
                    {bookings.map((booking) => (
                        <div key={booking.id} className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden flex flex-col md:flex-row">
                            <div className="md:w-64 h-48 md:h-auto overflow-hidden bg-gray-100">
                                {booking.propertyDetails?.image && (
                                    <img src={booking.propertyDetails.image} alt="Property" className="w-full h-full object-cover" />
                                )}
                            </div>

                            <div className="p-6 flex-grow flex flex-col justify-between">
                                <div className="flex justify-between items-start gap-4">
                                    <div>
                                        <div className="flex items-center gap-2 mb-2">
                                            <span className="text-xs font-bold text-gray-400 bg-gray-100 px-2 py-1 rounded">ID: {booking.id}</span>
                                            {booking.estado === 'COMPLETADO' && <span className="flex items-center text-xs font-bold text-green-700 bg-green-100 px-2 py-1 rounded"><CheckCircle size={12} className="mr-1" /> PAGO COMPLETADO</span>}
                                            {booking.estado === 'PENDIENTE' && <span className="flex items-center text-xs font-bold text-yellow-700 bg-yellow-100 px-2 py-1 rounded"><Clock size={12} className="mr-1" /> PAGO PENDIENTE</span>}
                                            {booking.estado === 'CANCELADO' && <span className="flex items-center text-xs font-bold text-red-700 bg-red-100 px-2 py-1 rounded"><CalendarX size={12} className="mr-1" /> CANCELADO</span>}
                                        </div>
                                        <h3 className="text-xl font-bold text-gray-800 line-clamp-1">{booking.propertyDetails?.name || 'Habitación'}</h3>
                                        <div className="text-gray-500 text-sm mt-1 flex gap-4">
                                            <p><strong>Check-In:</strong> {booking.fechaInicio}</p>
                                            <p><strong>Check-Out:</strong> {booking.fechaFin}</p>
                                        </div>
                                    </div>
                                    <div className="text-right">
                                        <p className="text-gray-500 text-sm">Total Facturado</p>
                                        <p className="text-2xl font-bold text-gray-800">${booking.totalPagar?.toFixed(2)}</p>
                                    </div>
                                </div>

                                <div className="mt-6 flex justify-end gap-3 pt-4 border-t border-gray-100">
                                    {booking.estado === 'PENDIENTE' && (
                                        <button
                                            onClick={() => handleOpenEditModal(booking)}
                                            className="flex items-center gap-1 text-secondary hover:text-white border border-secondary hover:bg-secondary font-semibold px-4 py-2 rounded-lg transition-colors text-sm"
                                        >
                                            <Pencil size={14} /> Editar Fechas
                                        </button>
                                    )}
                                    {(booking.estado === 'PENDIENTE' || booking.estado === 'COMPLETADO') && (
                                        <button
                                            onClick={() => handleOpenCancelModal(booking.id)}
                                            className="text-red-500 hover:text-white border border-red-500 hover:bg-red-500 font-semibold px-4 py-2 rounded-lg transition-colors text-sm"
                                        >
                                            Cancelar Reserva
                                        </button>
                                    )}
                                    {booking.estado === 'PENDIENTE' && (
                                        <button
                                            onClick={() => handleOpenPayModal(booking)}
                                            className="flex items-center gap-1 bg-secondary text-white font-semibold px-5 py-2 rounded-lg hover:opacity-90 transition-opacity text-sm"
                                        >
                                            <CreditCard size={14} /> Completar Pago
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))}
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
                        <h3 className="text-xl font-bold text-gray-800 mb-2">¿Cancelar Reserva?</h3>
                        <p className="text-gray-500 mb-6">
                            Estás a punto de cancelar esta reserva. Esta acción no se puede deshacer. Si ya habías pagado, el reembolso podría tomar de 3 a 5 días hábiles según las políticas del hotel.
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

            {/* Edit Dates Modal */}
            {editModalOpen && editBooking && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
                    <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6">
                        <div className="flex justify-between items-start mb-4">
                            <div className="bg-secondary/10 p-3 rounded-full flex-shrink-0">
                                <Calendar className="text-secondary" size={24} />
                            </div>
                            <button
                                onClick={() => setEditModalOpen(false)}
                                className="text-gray-400 hover:text-gray-600 transition-colors"
                            >
                                <X size={20} />
                            </button>
                        </div>
                        <h3 className="text-xl font-bold text-gray-800 mb-1">Modificar Fechas</h3>
                        <p className="text-gray-500 text-sm mb-5">{editBooking.propertyDetails?.name || 'Reserva'}</p>

                        <div className="space-y-4 mb-5">
                            <div>
                                <label className="text-sm font-bold text-gray-700 mb-1 block">Nuevo Check-In</label>
                                <input
                                    type="date"
                                    value={editCheckIn}
                                    onChange={(e) => setEditCheckIn(e.target.value)}
                                    className="w-full px-3 py-2 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-secondary/50 text-sm"
                                />
                            </div>
                            <div>
                                <label className="text-sm font-bold text-gray-700 mb-1 block">Nuevo Check-Out</label>
                                <input
                                    type="date"
                                    value={editCheckOut}
                                    onChange={(e) => setEditCheckOut(e.target.value)}
                                    className="w-full px-3 py-2 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-secondary/50 text-sm"
                                />
                            </div>
                        </div>

                        {editError && (
                            <div className="bg-red-50 text-red-600 p-3 rounded-lg text-sm font-medium border border-red-100 mb-4">
                                {editError}
                            </div>
                        )}

                        <div className="flex gap-3 justify-end">
                            <button
                                onClick={() => setEditModalOpen(false)}
                                disabled={isSavingEdit}
                                className="px-5 py-2 font-semibold text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors disabled:opacity-50"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={confirmEdit}
                                disabled={isSavingEdit}
                                className="flex items-center gap-2 px-5 py-2 font-semibold text-white bg-secondary rounded-lg hover:opacity-90 transition-all shadow-sm shadow-secondary/30 disabled:opacity-70"
                            >
                                {isSavingEdit && <Loader2 size={16} className="animate-spin" />}
                                Guardar Cambios
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Pay Modal */}
            {payModalOpen && bookingToPay && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
                    <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6">
                        <div className="flex justify-between items-start mb-4">
                            <div className="bg-secondary/10 p-3 rounded-full flex-shrink-0">
                                <CreditCard className="text-secondary" size={24} />
                            </div>
                            <button
                                onClick={() => setPayModalOpen(false)}
                                className="text-gray-400 hover:text-gray-600 transition-colors"
                            >
                                <X size={20} />
                            </button>
                        </div>
                        <h3 className="text-xl font-bold text-gray-800 mb-1">Completar Pago</h3>
                        <p className="text-gray-500 text-sm mb-2">{bookingToPay.propertyDetails?.name || 'Reserva'}</p>

                        <div className="bg-gray-50 p-4 rounded-lg space-y-2 mb-5 text-sm">
                            <div className="flex justify-between text-gray-600">
                                <span>Check-In</span>
                                <span className="font-semibold text-gray-800">{bookingToPay.fechaInicio}</span>
                            </div>
                            <div className="flex justify-between text-gray-600">
                                <span>Check-Out</span>
                                <span className="font-semibold text-gray-800">{bookingToPay.fechaFin}</span>
                            </div>
                            <hr className="my-2 border-gray-200" />
                            <div className="flex justify-between font-bold text-lg text-gray-800">
                                <span>Total (USD)</span>
                                <span>${bookingToPay.totalPagar?.toFixed(2)}</span>
                            </div>
                        </div>

                        {payError && (
                            <div className="bg-red-50 text-red-600 p-3 rounded-lg text-sm font-medium border border-red-100 mb-4">
                                {payError}
                            </div>
                        )}

                        <div className="relative z-0">
                            <PayPalScriptProvider options={{ "client-id": "Abmb2cDXKSlNoWSgW7vDLPRPJiYgp_oXvqpyGro0K33IePlPQBbqIaGOMxZaPZnn8_4duJNWZy0XaOe5", currency: "USD" }}>
                                <PayPalButtons
                                    style={{ layout: "vertical", color: "blue", shape: "rect" }}
                                    createOrder={(data, actions) => {
                                        return actions.order.create({
                                            purchase_units: [{ amount: { value: bookingToPay.totalPagar?.toFixed(2) } }]
                                        });
                                    }}
                                    onApprove={(data, actions) => {
                                        return handlePaymentSuccess(data);
                                    }}
                                />
                            </PayPalScriptProvider>
                        </div>
                        <p className="text-xs text-center text-gray-400 mt-3">Transacción segura y encriptada</p>
                    </div>
                </div>
            )}
        </div>
    );
}
