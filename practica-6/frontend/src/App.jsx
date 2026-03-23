import React, { useState, useEffect } from 'react';
import { Plus, History, X, ArrowLeft, Search, Calendar } from 'lucide-react';
import logo from './assets/img/Logo PUCMM.webp';

const API_BASE_URL = "https://x0fidptgzk.execute-api.us-east-1.amazonaws.com/";

// --- COMPONENTE: MODAL DE REGISTRO ---
const RegistroModal = ({ isOpen, onClose, onRefresh }) => {
  const [formData, setFormData] = useState({
    id_estudiante: '',
    nombre: '',
    correo: '',
    laboratorio: '',
    horario: ''
  });

  if (!isOpen) return null;

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.horario) {
      alert("Por favor seleccione fecha y hora");
      return;
    }

    const timePart = formData.horario.split('T')[1];
    if (timePart) {
      const [hourStr, minStr] = timePart.split(':');
      const hour = parseInt(hourStr, 10);
      const min = parseInt(minStr, 10);

      if (min !== 0) {
        alert("La hora debe ser exacta (ej. 14:00:00)");
        return;
      }
      if (hour < 8 || hour > 22 || (hour === 22 && min > 0)) { // 08:00 AM to 10:00 PM
        alert("La hora debe estar en el rango de 08:00 AM a 10:00 PM");
        return;
      }
    }

    const formattedHorario = formData.horario.length === 16 ? `${formData.horario}:00` : formData.horario;

    try {
      const response = await fetch(`${API_BASE_URL}/reservas`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...formData,
          horario: formattedHorario
        })
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        alert(`Error: ${errorData.message || errorData.error || 'No se pudo completar el registro'}`);
        return;
      }

      alert("Reserva exitosa");
      setFormData({ id_estudiante: '', nombre: '', correo: '', laboratorio: '', horario: '' });
      onClose();
      if (onRefresh) onRefresh();
    } catch (error) {
      alert(`Error de sistema: ${error.message}`);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex justify-center items-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-md border-2 border-gray-800 animate-in fade-in zoom-in duration-200">
        <div className="p-4 border-b-2 border-gray-800 flex justify-between items-center bg-gray-50">
          <h3 className="text-xl font-bold italic text-gray-800">Registro Reserva</h3>
          <button type="button" onClick={onClose} className="hover:bg-gray-200 rounded-full p-1"><X size={24} /></button>
        </div>
        <form className="p-6 space-y-3" onSubmit={handleSubmit}>
          <div>
            <label className="block font-bold mb-1">ID</label>
            <input type="text" name="id_estudiante" value={formData.id_estudiante} onChange={handleChange} required className="w-full border-2 border-gray-800 p-2 rounded shadow-[2px_2px_0px_0px_rgba(0,0,0,0.1)]" />
          </div>
          <div>
            <label className="block font-bold mb-1">Nombre</label>
            <input type="text" name="nombre" value={formData.nombre} onChange={handleChange} required className="w-full border-2 border-gray-800 p-2 rounded shadow-[2px_2px_0px_0px_rgba(0,0,0,0.1)]" />
          </div>
          <div>
            <label className="block font-bold mb-1">Correo</label>
            <input type="email" name="correo" value={formData.correo} onChange={handleChange} required className="w-full border-2 border-gray-800 p-2 rounded shadow-[2px_2px_0px_0px_rgba(0,0,0,0.1)]" />
          </div>
          <div>
            <label className="block font-bold mb-1">Laboratorio</label>
            <select name="laboratorio" value={formData.laboratorio} onChange={handleChange} required className="w-full border-2 border-gray-800 p-2 rounded shadow-[2px_2px_0px_0px_rgba(0,0,0,0.1)]">
              <option value="">Seleccionar Laboratorio</option>
              <option value="REDES">REDES</option>
              <option value="Computación">Computación</option>
              <option value="Comunicaciones">Comunicaciones</option>
            </select>
          </div>
          <div>
            <label className="block font-bold mb-1">Fecha Reserva</label>
            <input type="datetime-local" name="horario" value={formData.horario} onChange={handleChange} required className="w-full border-2 border-gray-800 p-2 rounded shadow-[2px_2px_0px_0px_rgba(0,0,0,0.1)]" />
          </div>
          <div className="flex justify-center gap-4 pt-4">
            <button type="submit" className="px-8 py-1 border-2 border-gray-800 rounded-[10px] font-bold hover:bg-gray-100 shadow-[3px_3px_0px_0px_rgba(0,0,0,1)] active:shadow-none transition-all">OK</button>
            <button type="button" onClick={onClose} className="px-6 py-1 border-2 border-gray-800 rounded-[10px] font-bold hover:bg-gray-100 shadow-[3px_3px_0px_0px_rgba(0,0,0,1)] active:shadow-none transition-all">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
};

// --- COMPONENTE PRINCIPAL ---
function App() {
  const formatearFecha = (item) => {
    const rawFecha = item.fecha_hora || item.horario || item.fecha;
    if (!rawFecha) return '-';
    try {
      const d = new Date(rawFecha);
      if (isNaN(d.getTime())) return rawFecha.replace('T', ' ');
      return d.toLocaleString('es-DO', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit', hour12: true });
    } catch {
      return rawFecha.replace('T', ' ');
    }
  };

  const [view, setView] = useState('activas'); // 'activas' o 'pasadas'
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Estado para reservas e historial
  const [reservas, setReservas] = useState([]);
  const [historial, setHistorial] = useState([]);

  // Estados para filtros
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');

  const fetchActivas = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/activas`);
      if (response.ok) {
        const data = await response.json();
        const mappedData = data.map(item => ({
          id: item.id_estudiante || item.id || '-',
          nombre: item.nombre || '-',
          lab: item.laboratorio || item.lab || '-',
          fecha: formatearFecha(item)
        }));
        setReservas(mappedData);
      }
    } catch (error) {
      console.error("Error fetching activas:", error);
    }
  };

  const fetchPasadas = async () => {
    if (!fechaInicio || !fechaFin) {
      alert("Por favor seleccione fecha de inicio y fin.");
      return;
    }
    try {
      const response = await fetch(`${API_BASE_URL}/pasadas?desde=${fechaInicio}&hasta=${fechaFin}`);
      if (response.ok) {
        const data = await response.json();
        const mappedData = data.map(item => ({
          id: item.id_estudiante || item.id || '-',
          nombre: item.nombre || '-',
          lab: item.laboratorio || item.lab || '-',
          fecha: formatearFecha(item)
        }));
        setHistorial(mappedData);
      } else {
        const errorData = await response.json().catch(() => ({}));
        alert(`Error: ${errorData.message || 'Error al buscar historial'}`);
      }
    } catch (error) {
      console.error("Error fetching pasadas:", error);
      alert("Error de conexión al cargar el historial");
    }
  };

  useEffect(() => {
    fetchActivas();
  }, []);

  return (
    <div className="min-h-screen bg-white font-poppins text-gray-800 pb-20">

      <div className="max-w-5xl mx-auto p-6">
        {/* Header Superior */}
        <div className="flex justify-between items-start mb-10">
          <img src={logo} alt="Logo PUCMM" className="h-17 w-auto -mt-4" />

          {view === 'activas' ? (
            <button onClick={() => setView('pasadas')} className="text-blue-600 flex items-center gap-2 hover:underline font-bold decoration-2 underline-offset-4 mt-2">
              <History size={20} /> Registros Pasados
            </button>
          ) : (
            <button onClick={() => setView('activas')} className="text-gray-600 flex items-center gap-2 hover:underline font-bold decoration-2 underline-offset-4 mt-2">
              <ArrowLeft size={20} /> Volver a Reservas
            </button>
          )}
        </div>

        {/* Título Principal */}
        <h1 className="text-4xl text-center mb-12 font-semibold decoration-gray-300">
          {view === 'activas' ? 'Reservas de Laboratorio - EICT' : 'Historial de Registros Pasados'}
        </h1>

        {/* --- SECCIÓN DE FILTROS / CONTROLES --- */}
        <div className="mb-8">
          {view === 'activas' ? (
            <button
              onClick={() => setIsModalOpen(true)}
              className="border-2 border-gray-800 px-6 py-2 rounded-[10px] shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] hover:translate-y-0.5 active:shadow-none transition-all font-bold bg-white text-lg"
            >
              Agregar Reserva
            </button>
          ) : (
            <div className="bg-gray-50 border-2 border-dashed border-gray-400 p-6 rounded-lg flex flex-wrap items-end gap-6 shadow-inner">
              <div className="flex-1 min-w-[200px]">
                <label className="block text-sm font-black mb-1 uppercase tracking-tighter text-gray-600">Fecha Inicio</label>
                <div className="relative">
                  <input type="date" value={fechaInicio} onChange={(e) => setFechaInicio(e.target.value)} className="w-full border-2 border-gray-800 p-2 rounded bg-white font-mono" />
                </div>
              </div>
              <div className="flex-1 min-w-[200px]">
                <label className="block text-sm font-black mb-1 uppercase tracking-tighter text-gray-600">Fecha Fin</label>
                <input type="date" value={fechaFin} onChange={(e) => setFechaFin(e.target.value)} className="w-full border-2 border-gray-800 p-2 rounded bg-white font-mono" />
              </div>
              <button onClick={fetchPasadas} className="bg-gray-800 text-white px-8 py-2.5 rounded-[10px] flex items-center gap-2 hover:bg-black transition-all font-bold shadow-[4px_4px_0px_0px_rgba(0,0,0,0.2)]">
                <Search size={20} /> Buscar Registros
              </button>
            </div>
          )}
        </div>

        {/* --- TABLA DE DATOS --- */}
        <div className="border-2 border-gray-800 rounded-[10px] overflow-hidden shadow-[8px_8px_0px_0px_rgba(0,0,0,0.05)] bg-white">
          <table className="w-full text-center border-collapse">
            <thead>
              <tr className="border-b-2 border-gray-800 bg-gray-50 font-bold">
                <th className="p-4 border-r-2 border-gray-800 w-1/4 text-xl">ID</th>
                <th className="p-4 border-r-2 border-gray-800 w-1/4 text-xl">Nombre</th>
                <th className="p-4 border-r-2 border-gray-800 w-1/4 text-xl">Laboratorio</th>
                <th className="p-4 w-1/4 text-xl">Fecha y Hora</th>
              </tr>
            </thead>
            <tbody className="divide-y-2 divide-gray-800">
              {(view === 'activas' ? reservas : historial).map((res, index) => (
                <tr key={index} className="hover:bg-blue-50 transition-colors">
                  <td className="p-4 border-r-2 border-gray-800 font-mono">{res.id}</td>
                  <td className="p-4 border-r-2 border-gray-800">{res.nombre}</td>
                  <td className="p-4 border-r-2 border-gray-800 font-bold text-gray-600">{res.lab}</td>
                  <td className="p-4 text-sm font-semibold">{res.fecha}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {(view === 'pasadas' && historial.length === 0) && (
            <div className="p-20 text-center text-gray-400 italic text-xl">
              No se encontraron registros en el rango seleccionado.
            </div>
          )}

          {(view === 'activas' && reservas.length === 0) && (
            <div className="p-20 text-center text-gray-400 italic text-xl">
              No hay reservas activas en este momento.
            </div>
          )}
        </div>
      </div>

      <RegistroModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} onRefresh={fetchActivas} />
    </div>
  );
}

export default App;