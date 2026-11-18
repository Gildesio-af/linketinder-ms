package zg.acelera.msnucleo.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import zg.acelera.msnucleo.dto.address.AddressCreateDTO
import zg.acelera.msnucleo.dto.address.AddressResponseDTO
import zg.acelera.msnucleo.dto.address.AddressUpdateDTO
import zg.acelera.msnucleo.service.AddressService

@RestController
@RequestMapping("/addresses")
class AddressController {
    private final AddressService addressService

    AddressController(AddressService addressService) {
        this.addressService = addressService
    }

    @GetMapping("/{id}")
    ResponseEntity<AddressResponseDTO> findById(@PathVariable("id") UUID id) {
        ResponseEntity.ok(addressService.findById(id))
    }

    @GetMapping
    ResponseEntity<Page<AddressResponseDTO>> findAll(Pageable pageable) {
        ResponseEntity.ok(addressService.findAll(pageable))
    }

    @GetMapping("/user/{userId}")
    ResponseEntity<List<AddressResponseDTO>> findByUserId(@PathVariable("userId") UUID userId) {
        ResponseEntity.ok(addressService.findByUserId(userId))
    }

    @PostMapping
    ResponseEntity<AddressResponseDTO> save(@Valid @RequestBody AddressCreateDTO dto) {
        AddressResponseDTO saved = addressService.save(dto)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id)
                .toUri()
        ResponseEntity.created(location).body(saved)
    }

    @PutMapping("/{id}")
    ResponseEntity<AddressResponseDTO> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AddressUpdateDTO dto) {
        ResponseEntity.ok(addressService.update(id, dto))
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        addressService.delete(id)
        ResponseEntity.noContent().build()
    }
}
