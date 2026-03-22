import React, { useState } from 'react';
import { Plus, History, X, ArrowLeft, Search, Calendar } from 'lucide-react';
import logo from './assets/img/Logo PUCMM.webp';

// --- COMPONENTE: MODAL DE REGISTRO ---
const RegistroModal = ({ isOpen, onClose }) => {
  if (!isOpen) return null;
  return (
    <div className="fixed inset-0 bg-black/50 flex justify-center items-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-md border-2 border-gray-800 animate-in fade-in zoom-in duration-200">
        <div className="p-4 border-b-2 border-gray-800 flex justify-between items-center bg-gray-50">
          <h3 className="text-xl font-bold italic text-gray-800">Registro Reserva</h3>
          <button onClick={onClose} className="hover:bg-gray-200 rounded-full p-1"><X size={24} /></button>
        </div>
        <form className="p-6 space-y-3">
          <div><label className="block font-bold mb-1">ID</label><input type="text" className="w-full border-2 border-gray-800 p-2 rounded shadow-[2px_2px_0px_0px_rgba(0,0,0,0.1)]" /></div>
          <div><label className="block font-bold mb-1">Nombre</label><input type="text" className="w-full border-2 border-gray-800 p-2 rounded shadow-[2px_2px_0px_0px_rgba(0,0,0,0.1)]" /></div>
          <div><label className="block font-bold mb-1">Carrera</label><input type="text" className="w-full border-2 border-gray-800 p-2 rounded shadow-[2px_2px_0px_0px_rgba(0,0,0,0.1)]" /></div>
          <div>
            <label className="block font-bold mb-1">Laboratorio</label>
            <select className="w-full border-2 border-gray-800 p-2 rounded shadow-[2px_2px_0px_0px_rgba(0,0,0,0.1)]">
              <option>Seleccionar Laboratorio</option>
              <option>REDES</option><option>Computación</option><option>Comunicaciones</option>
            </select>
          </div>
          <div><label className="block font-bold mb-1">Fecha Reserva</label><input type="datetime-local" className="w-full border-2 border-gray-800 p-2 rounded shadow-[2px_2px_0px_0px_rgba(0,0,0,0.1)]" /></div>
          <div className="flex justify-center gap-4 pt-4">
            <button type="button" className="px-8 py-1 border-2 border-gray-800 rounded-[10px] font-bold hover:bg-gray-100 shadow-[3px_3px_0px_0px_rgba(0,0,0,1)] active:shadow-none transition-all">OK</button>
            <button type="button" onClick={onClose} className="px-6 py-1 border-2 border-gray-800 rounded-[10px] font-bold hover:bg-gray-100 shadow-[3px_3px_0px_0px_rgba(0,0,0,1)] active:shadow-none transition-all">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
};

// --- COMPONENTE PRINCIPAL ---
function App() {
  const [view, setView] = useState('activas'); // 'activas' o 'pasadas'
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Estado para reservas e historial
  const [reservas, setReservas] = useState([]);
  const [historial, setHistorial] = useState([]);

  return (
    <div className="min-h-screen bg-white font-poppins text-gray-800 pb-20">

      <div className="max-w-5xl mx-auto p-6">
        {/* Header Superior */}
        <div className="flex justify-between items-start mb-10">
          <img src={logo} alt="Logo PUCMM" className="h-17 w-auto -mt-4" />

          {view === 'activas' ? (
            <button onClick={() => setView('pasadas')} className="text-blue-600 flex items-center gap-2 hover:underline font-bold decoration-2 underline-offset-4">
              <History size={20} /> Registros Pasados
            </button>
          ) : (
            <button onClick={() => setView('activas')} className="text-gray-600 flex items-center gap-2 hover:underline font-bold decoration-2 underline-offset-4">
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
                  <input type="date" className="w-full border-2 border-gray-800 p-2 rounded bg-white font-mono" />
                </div>
              </div>
              <div className="flex-1 min-w-[200px]">
                <label className="block text-sm font-black mb-1 uppercase tracking-tighter text-gray-600">Fecha Fin</label>
                <input type="date" className="w-full border-2 border-gray-800 p-2 rounded bg-white font-mono" />
              </div>
              <button className="bg-gray-800 text-white px-8 py-2.5 rounded-[10px] flex items-center gap-2 hover:bg-black transition-all font-bold shadow-[4px_4px_0px_0px_rgba(0,0,0,0.2)]">
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

      <RegistroModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} />
    </div>
  );
}

export default App;