# CREATE DATABASE laputa;

DROP TABLE IF EXISTS `users`;
CREATE TABLE users (
  email varchar(255) NOT NULL,
  appName varchar(255) NOT NULL,
  region text(65535),
  name text(65535),
  pass text(65535),
  last_modified timestamp,
  last_logged timestamp,
  last_logged_ip text(65535),
  is_facebook_user bool,
  is_super_admin bool DEFAULT FALSE,
  energy int,
  json text(65535),
  PRIMARY KEY(email, appName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `redeem`;
CREATE TABLE redeem (
  token character(32) PRIMARY KEY,
  company text,
  isRedeemed boolean DEFAULT FALSE,
  reward integer NOT NULL DEFAULT 0,
  email text,
  version integer NOT NULL DEFAULT 1,
  ts timestamp
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE flashed_tokens (
  token character(32),
  app_name  varchar(255),
  email text,
  project_id bigint(64) NOT NULL,
  device_id bigint(64) NOT NULL,
  is_activated boolean DEFAULT FALSE,
  ts timestamp,
  PRIMARY KEY(token, app_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE purchase (
  email  varchar(255),
  reward integer NOT NULL,
  transactionId  varchar(255),
  price  double(16,6),
  ts timestamp NOT NULL DEFAULT NOW(),
  PRIMARY KEY (email, transactionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE reporting_raw_data (
  email  varchar(255),
  project_id bigint(64),
  device_id bigint(64),
  pin int(2) ,
  pinType char,
  ts timestamp,
  stringValue text,
  doubleValue  double(16,6),

  PRIMARY KEY (email, project_id, device_id, pin, pinType, ts)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE reporting_average_minute (
  email  varchar(255),
  project_id bigint(64),
  device_id bigint(64),
  pin int(2) ,
  pinType char,
  ts timestamp,
  value  double(16,6),
  PRIMARY KEY (email, project_id, device_id, pin, pinType, ts)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE reporting_average_hourly (
  email  varchar(255),
  project_id bigint(64),
  device_id bigint(64),
  pin int(2) ,
  pinType char,
  ts timestamp,
  value  double(16,6),
  PRIMARY KEY (email, project_id, device_id, pin, pinType, ts)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE reporting_average_daily (
  email  varchar(255),
  project_id bigint(64),
  device_id bigint(64),
  pin int(2) ,
  pinType char,
  ts timestamp,
  value  double(16,6),
  PRIMARY KEY (email, project_id, device_id, pin, pinType, ts)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE reporting_app_stat_minute (
  region  varchar(255),
  ts timestamp,
  active bigint(64),
  active_week bigint(64),
  active_month bigint(64),
  minute_rate bigint(64),
  connected bigint(64),
  online_apps bigint(64),
  online_hards bigint(64),
  total_online_apps bigint(64),
  total_online_hards bigint(64),
  registrations bigint(64),
  PRIMARY KEY (region, ts)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE reporting_app_command_stat_minute (
  region  varchar(255),
  ts timestamp,
  response bigint(64),
  register bigint(64),
  login bigint(64),
  load_profile bigint(64),
  app_sync bigint(64),
  sharing bigint(64),
  get_token bigint(64),
  ping bigint(64),
  activate bigint(64),
  deactivate bigint(64),
  refresh_token bigint(64),
  get_graph_data bigint(64),
  export_graph_data bigint(64),
  set_widget_property bigint(64),
  bridge bigint(64),
  hardware bigint(64),
  get_share_dash bigint(64),
  get_share_token bigint(64),
  refresh_share_token bigint(64),
  share_login bigint(64),
  create_project bigint(64),
  update_project bigint(64),
  delete_project bigint(64),
  hardware_sync bigint(64),
  internal bigint(64),
  sms bigint(64),
  tweet bigint(64),
  email bigint(64),
  push bigint(64),
  add_push_token bigint(64),
  create_widget bigint(64),
  update_widget bigint(64),
  delete_widget bigint(64),
  create_device bigint(64),
  update_device bigint(64),
  delete_device bigint(64),
  get_devices bigint(64),
  create_tag bigint(64),
  update_tag bigint(64),
  delete_tag bigint(64),
  get_tags bigint(64),
  add_energy bigint(64),
  get_energy bigint(64),
  get_server bigint(64),
  connect_redirect bigint(64),
  web_sockets bigint(64),
  eventor bigint(64),
  webhooks bigint(64),
  appTotal bigint(64),
  hardTotal bigint(64),

  PRIMARY KEY (region, ts)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE reporting_http_command_stat_minute (
  region  varchar(255),
  ts timestamp,
  is_hardware_connected bigint(64),
  is_app_connected bigint(64),
  get_pin_data bigint(64),
  update_pin bigint(64),
  email bigint(64),
  push bigint(64),
  get_project bigint(64),
  qr bigint(64),
  get_history_pin_data bigint(64),
  total bigint(64),
  PRIMARY KEY (region, ts)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
