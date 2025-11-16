export function timer(ms) {
    return new Promise(resolve => {
      setTimeout(() => {
        resolve(true);
      }, ms);
  });
}

export function determineDST(date) {
  // Create a date object
  const now = new Date(date);

  // Use Intl.DateTimeFormat to get the time zone name
  const options = { timeZone: 'America/New_York', timeZoneName: 'short' };
  const formatter = new Intl.DateTimeFormat('en-US', options);
  const parts = formatter.formatToParts(now);

  // Find the time zone part
  const timeZone = parts.find(part => part.type === 'timeZoneName').value;

  // Determine if it is EDT or EST
  if (timeZone.includes('EDT')) {
    return date + ' EDT';
  } else if (timeZone.includes('EST')) {
    return date + ' EST';
  } else {
    return date;
  }
}
