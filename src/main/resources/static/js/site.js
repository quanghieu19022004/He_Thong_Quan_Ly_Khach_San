(() => {
  const dateLocal = (date) => `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2,'0')}-${String(date.getDate()).padStart(2,'0')}`;
  const midnightToday = new Date(); midnightToday.setHours(0,0,0,0);
  document.querySelectorAll('[data-date-form]').forEach(form => {
    const checkIn = form.querySelector('[data-checkin]');
    const checkOut = form.querySelector('[data-checkout]');
    if (!checkIn || !checkOut) return;
    const today = dateLocal(midnightToday);
    const refresh = () => {
      checkIn.min = today;
      if (checkIn.value && checkIn.value < today) checkIn.value = today;
      if (checkIn.value) {
        const d = new Date(`${checkIn.value}T12:00:00`); d.setDate(d.getDate()+1);
        checkOut.min = dateLocal(d);
        if (checkOut.value && checkOut.value <= checkIn.value) checkOut.value = checkOut.min;
      } else checkOut.min = today;
      const box = form.querySelector('[data-price-estimate]');
      if (!box) return;
      const price = Number(box.dataset.nightlyRate);
      const totalNode = box.querySelector('[data-estimate-total]');
      const nightsNode = box.querySelector('[data-estimate-nights]');
      if (!checkIn.value || !checkOut.value || !Number.isFinite(price)) {totalNode.textContent='—'; nightsNode.textContent='Chọn ngày để xem số đêm';return;}
      const start = new Date(`${checkIn.value}T12:00:00`);
      const end = new Date(`${checkOut.value}T12:00:00`);
      const nights = Math.round((end-start)/86400000);
      if (nights < 1 || nights > 366) {totalNode.textContent='—';nightsNode.textContent='Ngày lưu trú chưa hợp lệ';return;}
      const weekend = form.querySelector('[data-pricing-plan]')?.value === 'WEEKEND';
      let sum=0;
      for(let i=0;i<nights;i++) {const night=new Date(start);night.setDate(start.getDate()+i);const day=night.getDay();sum+=price*(weekend && (day===5||day===6)?1.2:1);}
      totalNode.textContent = Math.round(sum).toLocaleString('vi-VN')+' ₫';
      nightsNode.textContent = `${nights} đêm · Giá dự kiến, chưa phải xác nhận cuối cùng`;
    };
    checkIn.addEventListener('change',refresh);
    checkOut.addEventListener('change',refresh);
    form.querySelector('[data-pricing-plan]')?.addEventListener('change',refresh);
    refresh();
  });
})();
