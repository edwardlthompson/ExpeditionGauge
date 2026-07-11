-- AA AppValidation bypass + whitelist ExpeditionGauge (FOSS root workaround).
-- Mirrors AA-Tweaker "patch custom apps" FlagOverrides.

DROP TRIGGER IF EXISTS aa_patched_apps;
DROP TRIGGER IF EXISTS aa_patched_apps_fix;
DROP TRIGGER IF EXISTS after_delete;

INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, stringVal, committed)
VALUES ('com.google.android.gms.car', 0, 'app_white_list', '', 'dev.foss.expeditiongauge', 0);

INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, stringVal, committed)
VALUES ('com.google.android.gms.car', 0, 'car_connect_broadcast_whitelist', '', 'dev.foss.expeditiongauge', 0);

INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, stringVal, committed)
VALUES ('com.google.android.projection.gearhead', 0, 'AppValidation__allowed_package_list', '', 'dev.foss.expeditiongauge', 0);

INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, stringVal, committed)
VALUES ('com.google.android.projection.gearhead', 0, 'AppValidation__blocked_packages_by_installer', '', '', 0);

INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
VALUES ('com.google.android.projection.gearhead', 0, 'AppValidation__should_bypass_validation', '', 1, 0);

INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
VALUES ('com.google.android.projection.gearhead', 0, 'AppValidation__play_install_api', '', 0, 0);

INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
VALUES ('com.google.android.projection.gearhead', 0, 'AppValidation__swallow_play_api_exception', '', 1, 0);

INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
VALUES ('com.google.android.projection.gearhead', 0, 'AppValidation__swallow_play_api_exception_return_value', '', 1, 0);

INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
VALUES ('com.google.android.gms.car', 0, 'should_bypass_validation', '', 1, 0);

INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
VALUES ('com.google.android.projection.gearhead', 0, 'CarProjectionValidator__filter_disabled_packages_in_ispackageallowed_method', '', 0, 0);

INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
VALUES ('com.google.android.projection.gearhead', 0, 'UnknownSources__allow_full_screen_apps', '', 1, 0);

DELETE FROM Flags WHERE name = 'app_black_list';
DELETE FROM Flags WHERE name = 'app_white_list';

CREATE TRIGGER aa_patched_apps AFTER DELETE ON FlagOverrides
BEGIN
  INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, stringVal, committed)
  VALUES ('com.google.android.gms.car', 0, 'app_white_list', '', 'dev.foss.expeditiongauge', 0);
  INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, stringVal, committed)
  VALUES ('com.google.android.gms.car', 0, 'car_connect_broadcast_whitelist', '', 'dev.foss.expeditiongauge', 0);
  INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, stringVal, committed)
  VALUES ('com.google.android.projection.gearhead', 0, 'AppValidation__allowed_package_list', '', 'dev.foss.expeditiongauge', 0);
  INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, stringVal, committed)
  VALUES ('com.google.android.projection.gearhead', 0, 'AppValidation__blocked_packages_by_installer', '', '', 0);
  INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
  VALUES ('com.google.android.projection.gearhead', 0, 'AppValidation__should_bypass_validation', '', 1, 0);
  INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
  VALUES ('com.google.android.projection.gearhead', 0, 'AppValidation__play_install_api', '', 0, 0);
  INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
  VALUES ('com.google.android.projection.gearhead', 0, 'AppValidation__swallow_play_api_exception', '', 1, 0);
  INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
  VALUES ('com.google.android.projection.gearhead', 0, 'AppValidation__swallow_play_api_exception_return_value', '', 1, 0);
  INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
  VALUES ('com.google.android.gms.car', 0, 'should_bypass_validation', '', 1, 0);
  INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
  VALUES ('com.google.android.projection.gearhead', 0, 'CarProjectionValidator__filter_disabled_packages_in_ispackageallowed_method', '', 0, 0);
  INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed)
  VALUES ('com.google.android.projection.gearhead', 0, 'UnknownSources__allow_full_screen_apps', '', 1, 0);
END;
