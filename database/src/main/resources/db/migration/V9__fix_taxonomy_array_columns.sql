-- V9: Change taxonomy array columns from TEXT[] to TEXT for JSON string storage

ALTER TABLE taxonomy
    ALTER COLUMN banding_codes TYPE TEXT USING array_to_string(banding_codes, ','),
    ALTER COLUMN com_name_codes TYPE TEXT USING array_to_string(com_name_codes, ','),
    ALTER COLUMN sci_name_codes TYPE TEXT USING array_to_string(sci_name_codes, ',');
